// SPDX-License-Identifier: Apache-2.0
package com.example.eip.exchange;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Optional convenience: when you run {@code spring-boot:run}, this feeds a rotating sample order (a plain
 * {@code java.util.Map}) into the route every 3 seconds so you can watch the Message/headers live in the
 * console. It is switched off in tests (see {@code src/test/resources/application.yaml},
 * {@code app.demo.enabled=false}) so the tests stay deterministic.
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
                Map<String, Object> order = new LinkedHashMap<>();
                order.put("orderId", "A-" + (1000 + i));
                order.put("country", COUNTRIES[(int) (i % COUNTRIES.length)]);
                order.put("amount", 100 + (int) i);
                e.getMessage().setBody(order);
            })
            .to("direct:orders");
    }
}
