// SPDX-License-Identifier: Apache-2.0
package com.example.eip.channel;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * The producing side of a Message Channel.
 *
 * <p>A <b>Message Endpoint</b> is where a route touches a channel: {@code from(...)} is a <i>consumer</i>
 * endpoint, {@code to(...)} is a <i>producer</i> endpoint. A <b>Message Channel</b> is the named URI that
 * connects them — here {@code seda:work}.
 *
 * <p>This route consumes from {@code direct:orders} and hands the message off to {@code seda:work}. Because
 * {@code seda:} is asynchronous, {@code .to("seda:work")} just enqueues the message and returns immediately
 * on the <i>caller's</i> thread; the actual work happens later on a background thread (see {@link ProcessRoute}).
 */
@Component
public class IntakeRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("direct:orders")                                        // (1) synchronous entry channel — runs on the caller's thread
            .routeId("intake")                                       //     always name a route (tracing, metrics, tests)
            .log("intake received '${body}' on thread ${threadName}")
            .to("seda:work");                                        // (2) async in-memory queue — hand off and return immediately
    }
}
