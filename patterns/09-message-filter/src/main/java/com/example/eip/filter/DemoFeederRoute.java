// SPDX-License-Identifier: Apache-2.0
package com.example.eip.filter;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Optional convenience: when you run {@code spring-boot:run}, this feeds a rotating sample order into
 * the filter every 3 seconds so you can watch messages being kept or dropped live in the console. It is
 * switched off in tests (see {@code src/test/resources/application.yaml}, {@code app.demo.enabled=false})
 * so the tests stay deterministic.
 */
@Component
public class DemoFeederRoute extends RouteBuilder {

    // A rotating mix: a real order (kept), a zero-value order (dropped), a test order (dropped).
    private static final Order[] SAMPLES = {
        new Order("A-1001", 999, false),   // real money, not a test  -> KEPT
        new Order("A-1002", 0, false),     // zero value              -> DROPPED
        new Order("A-1003", 250, true)     // flagged as a test order -> DROPPED
    };

    @Override
    public void configure() {
        from("timer:demo?period=3000")
            .autoStartup("{{app.demo.enabled}}")   // property resolves to true (run) / false (test)
            .routeId("demo-feeder")
            .process(e -> {
                long i = e.getMessage().getHeader("CamelTimerCounter", 1L, Long.class);
                e.getMessage().setBody(SAMPLES[(int) ((i - 1) % SAMPLES.length)]);
            })
            .to("direct:orders");
    }
}
