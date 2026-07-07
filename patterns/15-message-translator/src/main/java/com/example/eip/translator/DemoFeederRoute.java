// SPDX-License-Identifier: Apache-2.0
package com.example.eip.translator;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Optional convenience: when you run {@code spring-boot:run}, this feeds a rotating legacy JSON <b>String</b>
 * into the translator every 3 seconds so you can watch a live translation in the console. It is switched off
 * in tests (see {@code src/test/resources/application.yaml}, {@code app.demo.enabled=false}) so the tests
 * stay deterministic.
 */
@Component
public class DemoFeederRoute extends RouteBuilder {

    // Legacy shape: terse keys (id, cust, amt, cur) with amt in CENTS.
    private static final String[] LEGACY_ORDERS = {
        "{ \"id\": \"A-1001\", \"cust\": \"Acme Corp\", \"amt\": 123499, \"cur\": \"USD\" }",
        "{ \"id\": \"A-1002\", \"cust\": \"Globex\",    \"amt\":   4999, \"cur\": \"EUR\" }",
        "{ \"id\": \"A-1003\", \"cust\": \"Initech\",   \"amt\": 800000, \"cur\": \"GBP\" }"
    };

    @Override
    public void configure() {
        from("timer:demo?period=3000")
            .autoStartup("{{app.demo.enabled}}")   // property resolves to true (run) / false (test)
            .routeId("demo-feeder")
            .process(e -> {
                long i = e.getMessage().getHeader("CamelTimerCounter", 1L, Long.class);
                e.getMessage().setBody(LEGACY_ORDERS[(int) ((i - 1) % LEGACY_ORDERS.length)]);
            })
            .to("direct:orders");
    }
}
