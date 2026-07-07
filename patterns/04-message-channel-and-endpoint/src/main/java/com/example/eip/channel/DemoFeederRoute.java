// SPDX-License-Identifier: Apache-2.0
package com.example.eip.channel;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Optional convenience: when you run {@code spring-boot:run}, this feeds a sample order into
 * {@code direct:orders} every 3 seconds so you can watch the sync→async hand-off live in the console.
 * It is switched off in tests (see {@code src/test/resources/application.yaml},
 * {@code app.demo.enabled=false}) so the tests stay deterministic.
 */
@Component
public class DemoFeederRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("timer:demo?period=3000")
            .autoStartup("{{app.demo.enabled}}")   // property resolves to true (run) / false (test)
            .routeId("demo-feeder")
            .process(e -> {
                long i = e.getMessage().getHeader("CamelTimerCounter", 1L, Long.class);
                e.getMessage().setBody("order-" + (1000 + i));
            })
            .to("direct:orders");
    }
}
