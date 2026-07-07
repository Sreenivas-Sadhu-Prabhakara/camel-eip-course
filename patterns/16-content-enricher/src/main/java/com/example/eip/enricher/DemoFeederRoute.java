// SPDX-License-Identifier: Apache-2.0
package com.example.eip.enricher;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Optional convenience: when you run {@code spring-boot:run}, this feeds a rotating sample order into the
 * enricher every 3 seconds so you can watch live enrichment in the console (the {@code customerName} /
 * {@code customerTier} appear on the way out). {@code C-9} is deliberately unknown, so you can see the
 * enricher pass a "customer not found" order through untouched. It is switched off in tests
 * (see {@code src/test/resources/application.yaml}, {@code app.demo.enabled=false}) so tests stay
 * deterministic.
 */
@Component
public class DemoFeederRoute extends RouteBuilder {

    private static final String[] CUSTOMER_IDS = {"C-1", "C-2", "C-3", "C-9"};

    @Override
    public void configure() {
        from("timer:demo?period=3000")
            .autoStartup("{{app.demo.enabled}}")   // property resolves to true (run) / false (test)
            .routeId("demo-feeder")
            .process(e -> {
                long i = e.getMessage().getHeader("CamelTimerCounter", 1L, Long.class);
                String customerId = CUSTOMER_IDS[(int) (i % CUSTOMER_IDS.length)];
                e.getMessage().setBody(new Order("A-" + (1000 + i), customerId));
            })
            .to("direct:orders");
    }
}
