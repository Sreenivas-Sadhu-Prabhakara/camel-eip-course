// SPDX-License-Identifier: Apache-2.0
package com.example.eip.messaging;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * Integration test (named *IT so it runs under `mvn verify` with Docker, and is skipped by `mvn test`).
 * Starts a real ActiveMQ Artemis broker in a container and proves queue (one consumer) vs topic
 * (all subscribers) semantics.
 */
@CamelSpringBootTest
@SpringBootTest
@Testcontainers
class PointToPointAndPubSubIT {

    @Container
    static final GenericContainer<?> ARTEMIS =
            new GenericContainer<>(DockerImageName.parse("apache/activemq-artemis:2.42.0"))
                    .withEnv("ARTEMIS_USER", "camel")
                    .withEnv("ARTEMIS_PASSWORD", "camel")
                    .withEnv("ANONYMOUS_LOGIN", "false")
                    .withExposedPorts(61616, 8161);

    @DynamicPropertySource
    static void brokerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.artemis.broker-url",
                () -> "tcp://" + ARTEMIS.getHost() + ":" + ARTEMIS.getMappedPort(61616));
        registry.add("spring.artemis.user", () -> "camel");
        registry.add("spring.artemis.password", () -> "camel");
    }

    @Autowired
    CamelContext context;
    @Autowired
    ProducerTemplate template;

    @EndpointInject("mock:fulfillment-out")
    MockEndpoint fulfillment;
    @EndpointInject("mock:email-out")
    MockEndpoint email;
    @EndpointInject("mock:analytics-out")
    MockEndpoint analytics;

    @BeforeEach
    void reset() {
        MockEndpoint.resetMocks(context);
    }

    @Test
    void queueDeliversToTheSingleConsumer() throws Exception {
        fulfillment.expectedMessageCount(1);
        template.sendBody("direct:fulfillment", "order-42");
        fulfillment.assertIsSatisfied();
    }

    @Test
    void topicBroadcastsToEverySubscriber() throws Exception {
        email.expectedMessageCount(1);
        analytics.expectedMessageCount(1);
        template.sendBody("direct:order-events", "order-placed-42");
        MockEndpoint.assertIsSatisfied(context);
    }
}
