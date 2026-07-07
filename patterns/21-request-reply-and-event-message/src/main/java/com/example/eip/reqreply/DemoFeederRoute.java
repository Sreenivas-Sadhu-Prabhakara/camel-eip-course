// SPDX-License-Identifier: Apache-2.0
package com.example.eip.reqreply;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Optional convenience: when you run {@code spring-boot:run}, this exercises BOTH patterns every 3 seconds
 * so you can watch them live in the console — a synchronous Request-Reply for the auth code, then a
 * fire-and-forget Event Message. It is switched off in tests (see
 * {@code src/test/resources/application.yaml}, {@code app.demo.enabled=false}) so the tests stay deterministic.
 */
@Component
public class DemoFeederRoute extends RouteBuilder {

    /** The camel-spring-boot-starter publishes a ready-to-use ProducerTemplate bean; a real caller uses it exactly like this. */
    @Autowired
    private ProducerTemplate producer;

    @Override
    public void configure() {
        from("timer:demo?period=3000")
            .autoStartup("{{app.demo.enabled}}")   // property resolves to true (run) / false (test)
            .routeId("demo-feeder")
            .process(e -> {
                long i = e.getMessage().getHeader("CamelTimerCounter", 1L, Long.class);
                Order order = new Order("A-" + (1000 + i), 100 + (int) i);

                // Request-Reply (InOut): BLOCK until the payment route replies with an auth code.
                String auth = producer.requestBody("direct:authorize", order, String.class);

                // Event Message (InOnly): fire-and-forget — return immediately, no reply awaited.
                producer.sendBody("direct:orders", order);

                e.getMessage().setBody("order " + order.getOrderId() + " authorized as " + auth);
            })
            .log("Request-Reply got a reply -> ${body}; event emitted one-way");
    }
}
