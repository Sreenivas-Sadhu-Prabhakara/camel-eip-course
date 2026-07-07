// SPDX-License-Identifier: Apache-2.0
package com.example.eip.testing;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Two tiny routes that exist ONLY to give the test class something to test. This module is not about a
 * new EIP — it is about the Camel <em>test toolkit</em>. So keep these routes trivial and let the test
 * methods (see {@code src/test/java}) be the real teaching artifact.
 *
 * <ul>
 *   <li><b>sample</b> — an InOnly content-based split: "VIP" bodies go to {@code {{ep.a}}}, everything
 *       else goes to {@code {{ep.b}}}. The two targets are {@code {{property}}} placeholders so tests can
 *       point them at {@code mock:} endpoints and assert exactly which branch a message took.</li>
 *   <li><b>echo</b> — an InOut (request/reply) route that transforms the body and returns it, so a test
 *       can demonstrate {@code ProducerTemplate.requestBody(...)}.</li>
 * </ul>
 */
@Component
public class SampleRoute extends RouteBuilder {

    @Override
    public void configure() {
        // (A) InOnly route with one Simple predicate — a mock: target proves which branch ran.
        from("direct:orders")
            .routeId("sample")                                 // ALWAYS name a route (tests advise it by id)
            .log("sample received: ${body}")
            .choice()
                .when(simple("${body} contains 'VIP'"))        // the branch a MockEndpoint will assert on
                    .to("{{ep.a}}")                            // priority lane  (mock:priority in tests)
                .otherwise()
                    .to("{{ep.b}}")                            // standard lane  (mock:standard in tests)
            .end();

        // (B) InOut route for a request/reply demo — transform() sets the reply body.
        from("direct:echo")
            .routeId("echo")
            .transform(simple("echo:${body}"));                // the caller of requestBody(...) gets this back
    }
}
