// SPDX-License-Identifier: Apache-2.0
package com.example.eip.channel;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * The test IS the specification of the Message Channel &amp; Endpoint pattern.
 *
 * <p>In the test profile {@code ep.out} resolves to {@code mock:out} (see
 * {@code src/test/resources/application.yaml}), so we can assert exactly what crossed the
 * {@code seda:work} channel — no broker required. Because {@code seda:} is asynchronous, the
 * {@code MockEndpoint} assertions wait (default 10s) for the background hop to complete.
 */
@CamelSpringBootTest
@SpringBootTest
class MessageChannelAndEndpointTest {

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
    void messageCrossesTheChannelToTheOutEndpoint() throws Exception {
        out.expectedBodiesReceived("PING");

        template.sendBody("direct:orders", "PING");

        // seda: is asynchronous — assertIsSatisfied waits for the queued hop to finish.
        out.assertIsSatisfied();
    }

    @Test
    void processingRunsOnADifferentThreadThanTheCaller() throws Exception {
        out.expectedMessageCount(1);
        String callerThread = Thread.currentThread().getName();

        template.sendBody("direct:orders", "PING");

        out.assertIsSatisfied();

        // The process route stamped the thread it actually ran on. Because the hop is via seda:,
        // that is a background consumer thread, never the caller's (this JUnit) thread.
        String processingThread =
            out.getReceivedExchanges().get(0).getMessage().getHeader("processingThread", String.class);
        assertNotNull(processingThread, "process route should record the thread it ran on");
        assertNotEquals(callerThread, processingThread,
            "seda: should hand the message to a background consumer thread, not the caller's thread");
    }
}
