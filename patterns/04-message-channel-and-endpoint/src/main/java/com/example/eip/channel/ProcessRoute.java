// SPDX-License-Identifier: Apache-2.0
package com.example.eip.channel;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * The consuming side of the {@code seda:work} Message Channel.
 *
 * <p>{@code seda:work?concurrentConsumers=2} pulls messages off the in-memory queue using a pool of two
 * background threads — so this route runs on a <i>different</i> thread from whoever sent the order. We stamp
 * that thread name onto a header ({@code processingThread}) so the test can prove the async hand-off happened,
 * then forward to the {@code {{ep.out}}} placeholder (a {@code log:} endpoint when you run the app, a
 * {@code mock:} endpoint under test).
 */
@Component
public class ProcessRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("seda:work?concurrentConsumers=2")                      // (1) async consumer — 2 background worker threads
            .routeId("process")
            .setHeader("processingThread", simple("${threadName}"))  // (2) record which thread actually did the work
            .log("process handling '${body}' on thread ${threadName}")
            .to("{{ep.out}}");                                       // (3) placeholder terminal: log: (run) / mock: (test)
    }
}
