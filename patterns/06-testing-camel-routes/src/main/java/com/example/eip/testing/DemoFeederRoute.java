// SPDX-License-Identifier: Apache-2.0
package com.example.eip.testing;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Optional convenience: when you run {@code spring-boot:run}, this feeds a rotating sample order into the
 * {@code sample} route every 3 seconds so you can watch live routing (VIP vs standard) in the console. It
 * is switched OFF in tests (see {@code src/test/resources/application.yaml}, {@code app.demo.enabled=false})
 * so the tests stay deterministic.
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
                String body = (i % 2 == 0) ? ("VIP order #" + i) : ("standard order #" + i);
                e.getMessage().setBody(body);
            })
            .to("direct:orders");
    }
}
