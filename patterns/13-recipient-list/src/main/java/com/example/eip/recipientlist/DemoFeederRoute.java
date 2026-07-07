// SPDX-License-Identifier: Apache-2.0
package com.example.eip.recipientlist;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Optional convenience: when you run {@code spring-boot:run}, this feeds a rotating sample order into the
 * router every 3 seconds so you can watch the fan-out live in the console. The amounts alternate between
 * low and high values, so you'll see some orders reach only warehouse + invoicing and others also reach
 * analytics. It is switched off in tests (see {@code src/test/resources/application.yaml},
 * {@code app.demo.enabled=false}) so the tests stay deterministic.
 */
@Component
public class DemoFeederRoute extends RouteBuilder {

    // A mix of low (< 1000) and high (>= 1000) amounts so the dynamic recipient list is visible.
    private static final int[] AMOUNTS = {250, 5000, 800, 12000, 300};

    @Override
    public void configure() {
        from("timer:demo?period=3000")
            .autoStartup("{{app.demo.enabled}}")   // property resolves to true (run) / false (test)
            .routeId("demo-feeder")
            .process(e -> {
                long i = e.getMessage().getHeader("CamelTimerCounter", 1L, Long.class);
                int amount = AMOUNTS[(int) (i % AMOUNTS.length)];
                e.getMessage().setBody(new Order("A-" + (2000 + i), "IN", amount));
            })
            .to("direct:orders");
    }
}
