// SPDX-License-Identifier: Apache-2.0
package com.example.eip.dlq;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Optional convenience: when you run {@code spring-boot:run}, this feeds one order into the route every
 * 5 seconds — alternating a good order and a permanently-failing one — so you can watch the Dead Letter
 * Channel retry and then dead-letter live in the console. It is switched off in tests (see
 * {@code src/test/resources/application.yaml}, {@code app.demo.enabled=false}) so tests stay deterministic.
 */
@Component
public class DemoFeederRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("timer:demo?period=5000")
            .autoStartup("{{app.demo.enabled}}")   // property resolves to true (run) / false (test)
            .routeId("demo-feeder")
            .process(e -> {
                long i = e.getMessage().getHeader("CamelTimerCounter", 1L, Long.class);
                boolean fail = (i % 2 == 0);        // every other order has a "declined card"
                e.getMessage().setBody(new Order("A-" + (1000 + i), 100 + (int) i, fail));
            })
            .to("direct:orders");
    }
}
