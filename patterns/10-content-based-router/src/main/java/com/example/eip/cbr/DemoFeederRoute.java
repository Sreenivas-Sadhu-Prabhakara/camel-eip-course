// SPDX-License-Identifier: Apache-2.0
package com.example.eip.cbr;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Optional convenience: when you run {@code spring-boot:run}, this feeds a rotating sample order into
 * the router every 3 seconds so you can watch live routing in the console. It is switched off in tests
 * (see {@code src/test/resources/application.yaml}, {@code app.demo.enabled=false}) so the tests stay
 * deterministic.
 */
@Component
public class DemoFeederRoute extends RouteBuilder {

    private static final String[] COUNTRIES = {"IN", "DE", "US", "FR", "BR"};

    @Override
    public void configure() {
        from("timer:demo?period=3000")
            .autoStartup("{{app.demo.enabled}}")   // property resolves to true (run) / false (test)
            .routeId("demo-feeder")
            .process(e -> {
                long i = e.getMessage().getHeader("CamelTimerCounter", 1L, Long.class);
                String country = COUNTRIES[(int) (i % COUNTRIES.length)];
                e.getMessage().setBody(new Order("A-" + (1000 + i), country, 100 + (int) i));
            })
            .to("direct:orders");
    }
}
