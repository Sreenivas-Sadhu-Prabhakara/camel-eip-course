// SPDX-License-Identifier: Apache-2.0
package com.example.eip.firstroute;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Optional convenience: when you run {@code spring-boot:run}, this feeds a sample order into the route
 * every 3 seconds so you can watch it flow through {@code order-intake} in the console. It is switched
 * off in tests (see {@code src/test/resources/application.yaml}, {@code app.demo.enabled=false}) so the
 * tests stay deterministic.
 *
 * <p>Notice this feeder is ALSO a route — and it gets its own {@code .routeId("demo-feeder")}. Two routes,
 * two ids; that is exactly the naming discipline this lesson is about.
 */
@Component
public class DemoFeederRoute extends RouteBuilder {

    private static final String[] CUSTOMERS = {"Asha", "Bruno", "Chen", "Diego", "Elif"};

    @Override
    public void configure() {
        from("timer:demo?period=3000")
            .autoStartup("{{app.demo.enabled}}")   // property resolves to true (run) / false (test)
            .routeId("demo-feeder")
            .process(e -> {
                long i = e.getMessage().getHeader("CamelTimerCounter", 1L, Long.class);
                String customer = CUSTOMERS[(int) (i % CUSTOMERS.length)];
                e.getMessage().setBody(new Order("A-" + (1000 + i), customer));
            })
            .to("direct:orders");
    }
}
