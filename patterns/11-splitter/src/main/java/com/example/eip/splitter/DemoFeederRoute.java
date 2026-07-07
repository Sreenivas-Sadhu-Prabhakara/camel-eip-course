// SPDX-License-Identifier: Apache-2.0
package com.example.eip.splitter;

import java.util.List;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Optional convenience: when you run {@code spring-boot:run}, this feeds a sample 3-line bulk order into
 * the splitter every 3 seconds so you can watch a composite fan out into per-item messages in the
 * console. It is switched off in tests (see {@code src/test/resources/application.yaml},
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
                Order order = new Order("A-" + (1000 + i),
                        List.of("SKU-" + i + "-A", "SKU-" + i + "-B", "SKU-" + i + "-C"));
                e.getMessage().setBody(order);
            })
            .to("direct:orders");
    }
}
