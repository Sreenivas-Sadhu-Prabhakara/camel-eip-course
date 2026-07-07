// SPDX-License-Identifier: Apache-2.0
package com.example.eip.filter;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Message Filter (EIP): discard messages you don't care about so downstream never sees them.
 *
 * <p>Here we let an {@link Order} through only when it is a <em>real</em> order worth money:
 * {@code totalAmount > 0} AND it is NOT a test order ({@code testFlag == false}). Everything else is
 * <strong>silently dropped</strong> — the exchange simply reaches the end of the {@code filter()} block
 * and stops. The single output endpoint is a {@code {{ep.out}}} placeholder so production can point it
 * at a real {@code direct:}/{@code jms:} endpoint while tests point it at a {@code mock:} endpoint.
 * The numbered comments below are referenced one-for-one by the lesson page's annotated walkthrough.
 */
@Component
public class MessageFilterRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("direct:orders")                                         // (1) the channel orders arrive on
            .routeId("filter")                                        //     always name a route (tracing, metrics, tests)
            .log("Considering order ${body.orderId} (amount=${body.totalAmount}, test=${body.testFlag})")
            .filter(simple("${body.totalAmount} > 0 && ${body.testFlag} == false")) // (2) the Message Filter itself
                .log("Order ${body.orderId} passed the filter -> forwarding")       // (3) only survivors get here
                .to("{{ep.out}}")                                     // (4) the one destination for kept messages
            .end();                                                   // (5) messages that fail the predicate are dropped
    }
}
