// SPDX-License-Identifier: Apache-2.0
package com.example.eip.recipientlist;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Recipient List (EIP): route ONE message to a set of recipients that is computed at runtime.
 *
 * <p>Contrast with {@code multicast()}, whose {@code .to(...)} targets are fixed in the route at design
 * time. Here the targets come from a {@code recipients} header that {@link RecipientResolver} fills in per
 * message, so the same route can fan out to two systems for one order and three for the next. The endpoint
 * URIs the resolver returns are {@code {{property}}}-driven ({@code mock:} in tests, {@code log:}/real
 * systems in prod). The numbered comments below are referenced by the lesson page's annotated walkthrough.
 */
@Component
public class RecipientListRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("direct:orders")                                                 // (1) the channel orders arrive on
            .routeId("recipient-list")                                        //     always name a route (tracing, metrics, tests)
            .log("Fanning out order ${body.orderId} (amount ${body.totalAmount})")
            .setHeader("recipients", method(RecipientResolver.class, "recipientsFor")) // (2) compute the recipient list per message
            .recipientList(header("recipients"), ",");                        // (3) split on ',' and deliver a copy to each recipient
    }
}
