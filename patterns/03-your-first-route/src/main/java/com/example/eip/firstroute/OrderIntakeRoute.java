// SPDX-License-Identifier: Apache-2.0
package com.example.eip.firstroute;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Your first route — the anatomy of every Camel route.
 *
 * <p>Read a route <b>left-to-right</b>, top-to-bottom: it starts at ONE {@code from(...)} consumer, flows
 * through zero or more steps, and ends at a {@code to(...)} producer. That's the whole shape:
 *
 * <pre>
 *   from( consumer ) ... steps ... to( producer )
 * </pre>
 *
 * <p>The numbered comments below are referenced one-for-one by the lesson page's annotated walkthrough.
 */
@Component
public class OrderIntakeRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("direct:orders")                                   // (1) CONSUMER: the channel orders arrive on
            .routeId("order-intake")                            // (2) ALWAYS name the route — see the note below
            .log("Received order ${body.orderId} from ${body.customer}") // (3) a step: one line in the flow
            .to("{{ep.out}}");                                  // (4) PRODUCER: where the message goes next
    }

    // ─────────────────────────────────────────────────────────────────────────────────────────────
    // (2) Why routeId("order-intake")?
    //   Without it, Camel auto-generates an id like "route1", "route2", … based on start-up order.
    //   Those numbers shift the moment you add/remove/reorder a route, which quietly breaks:
    //     • tracing & logs  — "route1 failed" tells you nothing; "order-intake failed" is a lead
    //     • metrics         — Micrometer tags timers/counters by route id
    //     • management/tests — you stop, resume, or advice-with a route BY its id
    //   Rule of thumb: every from(...) gets a .routeId(...) on the very next line.
    // ─────────────────────────────────────────────────────────────────────────────────────────────
}
