// SPDX-License-Identifier: Apache-2.0
package com.example.eip.idempotent;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Optional convenience: when you run {@code spring-boot:run}, this simulates a flaky upstream that sends
 * every order <b>twice</b> (a classic double-submit / broker redelivery). Each order id is fed on two
 * consecutive timer ticks, so you will see the first copy reach {@code log:out} and the second copy get
 * silently dropped by the Idempotent Consumer.
 *
 * <p>It is switched off in tests (see {@code src/test/resources/application.yaml},
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
                // Advance the order id every 2 ticks -> each id is emitted twice in a row (a duplicate).
                long orderNum = 1000 + (i - 1) / 2;
                String orderId = "A-" + orderNum;
                e.getMessage().setHeader("orderId", orderId);          // the idempotency key
                e.getMessage().setBody(new Order(orderId, 100 + (int) orderNum));
            })
            .to("direct:orders");
    }
}
