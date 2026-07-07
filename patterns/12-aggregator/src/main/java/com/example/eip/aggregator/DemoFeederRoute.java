// SPDX-License-Identifier: Apache-2.0
package com.example.eip.aggregator;

import java.util.List;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Optional convenience: when you run {@code spring-boot:run}, this emits a fresh order every 6 seconds and
 * <b>splits</b> it into 3 line items fed into the Aggregator — so the console shows the whole
 * splitter&nbsp;&rarr;&nbsp;aggregator round-trip live. It is switched off in tests (see
 * {@code src/test/resources/application.yaml}, {@code app.demo.enabled=false}) so the tests stay deterministic.
 */
@Component
public class DemoFeederRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("timer:demo?period=6000")
            .autoStartup("{{app.demo.enabled}}")   // property resolves to true (run) / false (test)
            .routeId("demo-feeder")
            .process(e -> {
                long i = e.getMessage().getHeader("CamelTimerCounter", 1L, Long.class);
                String orderId = "A-" + (1000 + i);
                List<LineItem> items = List.of(
                        new LineItem("SKU-BOOK", 30),
                        new LineItem("SKU-PEN", 5),
                        new LineItem("SKU-BAG", 65));
                // orderId + total ride as headers; the split below copies them onto every item.
                e.getMessage().setHeader("orderId", orderId);
                e.getMessage().setHeader("total", items.size());
                e.getMessage().setBody(items);
            })
            .split(body())          // Splitter: one message per line item…
                .to("direct:items") // …each fed into the Aggregator, which re-assembles them.
            .end();
    }
}
