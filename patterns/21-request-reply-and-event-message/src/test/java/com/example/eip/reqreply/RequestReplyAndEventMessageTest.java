// SPDX-License-Identifier: Apache-2.0
package com.example.eip.reqreply;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ExchangePattern;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * The test IS the specification of Request-Reply vs Event Message.
 *
 * <p>Request-Reply is InOut: {@code requestBody(...)} blocks and hands back the correlated reply the
 * {@code payment} route produced. Event Message is InOnly: {@code sendBody(...)} returns immediately and
 * the {@code emit-event} route publishes one-way to {@code mock:event} (see
 * {@code src/test/resources/application.yaml}). No broker required.
 */
@CamelSpringBootTest
@SpringBootTest
class RequestReplyAndEventMessageTest {

    @Autowired
    CamelContext context;

    @Autowired
    ProducerTemplate template;

    @EndpointInject("mock:event")
    MockEndpoint event;

    @BeforeEach
    void resetExpectations() {
        // The Spring/Camel context is shared across test methods; clear mock state between tests.
        MockEndpoint.resetMocks(context);
    }

    @Test
    void requestReplyReturnsCorrelatedAuthCode() {
        // InOut: requestBody blocks and returns the reply the payment route set on the body.
        String auth = template.requestBody("direct:authorize", new Order("A-1001", 999), String.class);

        assertEquals("AUTH-A-1001", auth);
    }

    @Test
    void eventMessageIsFireAndForgetInOnly() throws Exception {
        event.expectedMessageCount(1);

        // InOnly: one event goes out; there is no reply to capture.
        template.sendBody("direct:orders", new Order("A-2002", 500));

        event.assertIsSatisfied();
        // Exactly one message, and it arrived one-way — the recorded pattern is InOnly (no reply channel).
        assertEquals(ExchangePattern.InOnly, event.getReceivedExchanges().get(0).getPattern());
    }
}
