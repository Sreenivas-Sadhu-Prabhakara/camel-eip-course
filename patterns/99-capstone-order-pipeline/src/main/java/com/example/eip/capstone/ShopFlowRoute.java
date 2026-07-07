// SPDX-License-Identifier: Apache-2.0
package com.example.eip.capstone;

import org.apache.camel.ExchangePattern;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.support.processor.idempotent.MemoryIdempotentRepository;
import org.springframework.stereotype.Component;

/**
 * The capstone: ShopFlow's end-to-end order pipeline, composed from eight EIPs learned in the course.
 * Every hop is in-memory (direct:/mock:) so it runs and tests green with no broker.
 *
 * <p>Flow: JSON in → <b>Message Translator</b> → <b>Message Filter</b> (drop test/zero orders) →
 * <b>Idempotent Consumer</b> (once per orderId) → <b>Wire Tap</b> (audit) → <b>Content-Based Router</b>
 * (region) → <b>Request-Reply</b> (payment auth) → <b>Recipient List / multicast</b> (warehouse + invoicing)
 * → <b>Event Message</b> (InOnly order-placed). A <b>Dead Letter Channel</b> catches payment declines.
 */
@Component
public class ShopFlowRoute extends RouteBuilder {

    @Override
    public void configure() {
        // Dead Letter Channel: a declined payment is retried once, then parked (not lost).
        errorHandler(deadLetterChannel("{{ep.dead}}").maximumRedeliveries(1).redeliveryDelay(10));

        // (1) Message Translator — JSON text to a canonical Order POJO.
        from("direct:orders").routeId("intake")
            .unmarshal().json(JsonLibrary.Jackson, Order.class)
            .to("direct:validate");

        // (2) Message Filter — QA/test and zero-value orders never continue.
        from("direct:validate").routeId("validate")
            .filter(simple("${body.totalAmount} > 0 && ${body.testFlag} == false"))
                .to("direct:dedupe")
            .end();

        // (3) Idempotent Consumer — each orderId is processed exactly once.
        from("direct:dedupe").routeId("dedupe")
            .idempotentConsumer(simple("${body.orderId}"),
                    MemoryIdempotentRepository.memoryIdempotentRepository())
                .to("direct:process")
            .end();

        // (4) Wire Tap (audit) + (5) Content-Based Router (region).
        from("direct:process").routeId("process")
            .wireTap("{{ep.audit}}")
            .choice()
                .when(simple("${body.country} == 'IN'")).setHeader("region", constant("domestic"))
                .otherwise().setHeader("region", constant("international"))
            .end()
            .to("direct:pay");

        // (6) Request-Reply — call the payment gateway (InOut) and keep the order as the body.
        from("direct:pay").routeId("pay")
            .setHeader("orderBackup", body())
            .to(ExchangePattern.InOut, "direct:authorize")   // body becomes the auth code (the reply)
            .setHeader("authCode", body())
            .setBody(header("orderBackup"))                   // restore the Order as the body
            .to("direct:fulfil");

        from("direct:authorize").routeId("payment-gateway")
            .process(e -> {
                Order o = e.getMessage().getBody(Order.class);
                if (o.isFraudulent()) {
                    throw new PaymentDeclinedException("payment declined for " + o.getOrderId());
                }
                e.getMessage().setBody("AUTH-" + o.getOrderId());
            });

        // (7) Recipient List / multicast fan-out + (8) Event Message (fire-and-forget).
        from("direct:fulfil").routeId("fulfil")
            .log("Fulfilling ${body.orderId} (auth ${header.authCode}) region=${header.region}")
            .multicast().to("{{ep.warehouse}}", "{{ep.invoicing}}").end()
            .setExchangePattern(ExchangePattern.InOnly).to("{{ep.event}}");
    }
}
