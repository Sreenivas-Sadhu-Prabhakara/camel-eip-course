// SPDX-License-Identifier: Apache-2.0
package com.example.eip.dlq;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Dead Letter Channel (EIP): when a message cannot be delivered, don't lose it — retry a few times,
 * then move it to a dedicated "dead letter" endpoint for later inspection or replay.
 *
 * <p>The {@code errorHandler(deadLetterChannel(...))} is scoped to THIS route. Its redelivery policy
 * says: on any exception, retry up to {@code maximumRedeliveries(3)} times, waiting
 * {@code redeliveryDelay(20)} ms between tries. If the message still fails after the last retry, the
 * Dead Letter Channel marks the exchange as handled and routes it to {@code {{ep.dead}}} — so the
 * caller of {@code direct:orders} does NOT see the exception (unlike Camel's default error handler,
 * which would rethrow it). The numbered comments are referenced by the lesson page's walkthrough.
 */
@Component
public class DeadLetterChannelRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("direct:orders")                                    // (1) the channel orders arrive on
            .routeId("dlq")                                      //     always name a route
            .errorHandler(deadLetterChannel("{{ep.dead}}")       // (2) the Dead Letter Channel itself
                .maximumRedeliveries(3)                          // (3) retry up to 3 times before giving up
                .redeliveryDelay(20))                            // (4) wait 20 ms between attempts (tiny = fast tests)
            .log("Charging order ${body.orderId} (amount ${body.amount})")
            .bean("paymentBean", "charge")                       // (5) may throw PaymentDeclinedException
            .log("Order ${body.orderId} paid OK")
            .to("{{ep.ok}}");                                    // (6) success channel — never reached on failure
    }
}
