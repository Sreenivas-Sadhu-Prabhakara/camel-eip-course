// SPDX-License-Identifier: Apache-2.0
package com.example.eip.cbr;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Content-Based Router (EIP): send each message to a different destination based on its content.
 *
 * <p>Here we route each {@link Order} by its destination {@code country}. The three branch targets are
 * NOT hardcoded — they are {@code {{property}}} placeholders so production can point them at real
 * {@code direct:}/{@code jms:} endpoints while tests point them at {@code mock:} endpoints. The numbered
 * comments below are referenced one-for-one by the lesson page's annotated walkthrough.
 */
@Component
public class ContentBasedRouterRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("direct:orders")                                     // (1) the channel orders arrive on
            .routeId("content-based-router")                      //     always name a route (tracing, metrics, tests)
            .log("Routing order ${body.orderId} bound for ${body.country}")
            .choice()                                             // (2) the Content-Based Router itself
                .when(simple("${body.country} == 'IN'"))         // (3) domestic
                    .to("{{ep.domestic}}")
                .when(simple("${body.country} regex '(DE|FR|ES|IT)'")) // (4) EU hub
                    .to("{{ep.eu}}")
                .otherwise()                                     // (5) ALWAYS handle the rest
                    .to("{{ep.international}}")
            .end();
    }
}
