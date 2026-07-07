// SPDX-License-Identifier: Apache-2.0
package com.example.eip.wiretap;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Wire Tap (EIP): send a <b>copy</b> of every message to a secondary channel without disturbing the
 * main flow.
 *
 * <p>Here every {@link Order} that arrives keeps travelling down the main pipeline, but a fire-and-forget
 * copy is also dropped onto an audit trail. The tap is <b>asynchronous</b> — Camel does not wait for the
 * audit channel before the main flow continues — and it does <b>not</b> alter the main exchange. Contrast
 * with Multicast / Recipient List, which fan a message out to several destinations as part of the main
 * route. The two endpoints are {@code {{property}}} placeholders so production can point them at real
 * {@code direct:}/{@code jms:} endpoints while tests point them at {@code mock:} endpoints. The numbered
 * comments below are referenced one-for-one by the lesson page's annotated walkthrough.
 */
@Component
public class WireTapRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("direct:orders")                                     // (1) the channel orders arrive on
            .routeId("wire-tap")                                  //     always name a route (tracing, metrics, tests)
            .log("Order ${body.orderId} — tapping a copy to audit, main flow continues")
            .wireTap("{{ep.audit}}")                              // (2) async fire-and-forget COPY to the audit trail
            .to("{{ep.main}}");                                   // (3) the main flow continues, untouched
    }
}
