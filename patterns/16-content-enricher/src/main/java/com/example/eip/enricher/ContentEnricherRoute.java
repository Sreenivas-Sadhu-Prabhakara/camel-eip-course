// SPDX-License-Identifier: Apache-2.0
package com.example.eip.enricher;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Content Enricher (EIP): augment a message with data it does not carry, fetched from another source.
 *
 * <p>An {@link Order} arrives knowing only its {@code customerId}. We use {@code enrich()} to call a
 * second route ({@code direct:customer-lookup}) that returns the full {@link Customer}, then a
 * {@link MergeStrategy} copies the customer fields onto the order. The lookup is a plain bean — NO real
 * HTTP — so the whole pattern runs in-memory and offline. The output endpoint is a {@code {{property}}}
 * placeholder so production points it at a real endpoint while tests point it at {@code mock:}.
 *
 * <p>Note: {@code enrich()} is the request/reply flavour (it drives the resource route on demand). Its
 * sibling {@code pollEnrich()} instead <em>consumes</em> from a resource (e.g. read a file / poll a queue)
 * — same merge idea, different trigger.
 */
@Component
public class ContentEnricherRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("direct:orders")                                          // (1) orders arrive knowing only customerId
            .routeId("enricher")
            .log("Enriching order ${body.orderId} for customer ${body.customerId}")
            .enrich("direct:customer-lookup", new MergeStrategy())     // (2) fetch + merge customer data
            .log("Enriched order: ${body}")
            .to("{{ep.out}}");                                         // (3) enriched order continues downstream

        from("direct:customer-lookup")                                 // (4) the enrich resource route
            .routeId("customer-lookup")
            .log("Looking up customer ${body.customerId}")
            .bean(CustomerRepository.class, "findById(${body.customerId})"); // (5) bean returns a Customer by id
    }
}
