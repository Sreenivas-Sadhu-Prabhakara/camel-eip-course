// SPDX-License-Identifier: Apache-2.0
package com.example.eip.messaging;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Point-to-Point vs Publish-Subscribe on a real broker (ActiveMQ Artemis via the JMS component).
 *
 * <ul>
 *   <li><b>Point-to-Point</b>: a {@code jms:queue:} — exactly ONE consumer gets each message.</li>
 *   <li><b>Publish-Subscribe</b>: a {@code jms:topic:} — EVERY subscriber gets a copy.</li>
 * </ul>
 *
 * <p>The same idea applies to Kafka with {@code camel-kafka-starter} and {@code kafka:} endpoints —
 * see the README.
 */
@Component
public class MessagingRoutes extends RouteBuilder {

    @Override
    public void configure() {
        // ----- Point-to-Point: one queue, one consumer -----
        from("direct:fulfillment").routeId("send-to-queue")
            .to("jms:queue:fulfillment");

        from("jms:queue:fulfillment").routeId("warehouse-worker")
            .to("{{ep.fulfillment-out}}");

        // ----- Publish-Subscribe: one topic, many subscribers -----
        from("direct:order-events").routeId("publish-events")
            .to("jms:topic:order-events");

        from("jms:topic:order-events").routeId("email-subscriber")
            .to("{{ep.email-out}}");

        from("jms:topic:order-events").routeId("analytics-subscriber")
            .to("{{ep.analytics-out}}");
    }
}
