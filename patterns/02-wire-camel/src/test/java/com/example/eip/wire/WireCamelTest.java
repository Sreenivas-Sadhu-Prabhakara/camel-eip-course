// SPDX-License-Identifier: Apache-2.0
package com.example.eip.wire;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * The test IS the specification for this module: it proves that adding one starter really did wire
 * Camel into the Spring Boot app, and that the {@link PingRoute} behaves deterministically.
 *
 * <p>In the test profile {@code {{ep.out}}} resolves to {@code mock:out} (see
 * {@code src/test/resources/application.yaml}) and the demo heartbeat is disabled, so we can assert
 * exactly one clean reply — no broker, no timer noise.
 */
@CamelSpringBootTest
@SpringBootTest
class WireCamelTest {

    @Autowired
    CamelContext context;

    @Autowired
    ProducerTemplate template;

    @EndpointInject("mock:out")
    MockEndpoint out;

    @BeforeEach
    void resetExpectations() {
        // The Spring/Camel context is shared across test methods; clear mock state between tests.
        MockEndpoint.resetMocks(context);
    }

    @Test
    void camelContextWasAutoCreatedAndRoutesDiscovered() {
        // Proof that the one starter wired Camel: the context exists and both @Component routes loaded.
        assertNotNull(context, "camel-spring-boot-starter should auto-create a CamelContext");
        assertNotNull(context.getRoute("ping"), "PingRoute should be auto-discovered");
        assertNotNull(context.getRoute("hello-timer"), "HelloTimerRoute should be auto-discovered");
    }

    @Test
    void pingGetsPongPrefix() throws Exception {
        out.expectedMessageCount(1);

        template.sendBody("direct:ping", "ping");

        MockEndpoint.assertIsSatisfied(context);

        String body = out.getReceivedExchanges().get(0).getMessage().getBody(String.class);
        assertTrue(body.startsWith("pong:"), "reply body should start with \"pong:\" but was: " + body);
    }

    @Test
    void pongPrependsTheOriginalBody() throws Exception {
        out.expectedBodiesReceived("pong:hello");

        template.sendBody("direct:ping", "hello");

        MockEndpoint.assertIsSatisfied(context);
    }
}
