// SPDX-License-Identifier: Apache-2.0
package com.example.eip.dlq;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * The test IS the specification of the Dead Letter Channel.
 *
 * <p>In the test profile the terminal endpoints resolve to {@code mock:ok} and {@code mock:dead}
 * (see {@code src/test/resources/application.yaml}), so we can assert exactly where each order ends
 * up — no broker required. The redelivery delay in the route is only 20 ms, so the retries finish
 * in well under a second.
 */
@CamelSpringBootTest
@SpringBootTest
class DeadLetterChannelTest {

    @Autowired
    CamelContext context;

    @Autowired
    ProducerTemplate template;

    @Autowired
    PaymentBean paymentBean;

    @EndpointInject("mock:ok")
    MockEndpoint ok;

    @EndpointInject("mock:dead")
    MockEndpoint dead;

    @BeforeEach
    void resetExpectations() {
        // The Spring/Camel context is shared across test methods; clear mock + bean state between tests.
        MockEndpoint.resetMocks(context);
        paymentBean.resetAttempts();
    }

    @Test
    void goodOrderIsPaidAndReachesOk() throws Exception {
        ok.expectedMessageCount(1);
        dead.expectedMessageCount(0);

        template.sendBody("direct:orders", new Order("A-1001", 999, false));

        MockEndpoint.assertIsSatisfied(context);
        // Charged exactly once — a success is never retried.
        assertEquals(1, paymentBean.getAttempts());
    }

    @Test
    void failingOrderLandsInDeadLetterAfterRetries() throws Exception {
        ok.expectedMessageCount(0);
        dead.expectedMessageCount(1);
        // The message that lands in the Dead Letter Channel was retried first
        // (proven by the attempts assertion below: 1 original + 3 redeliveries).

        template.sendBody("direct:orders", new Order("A-1002", 500, true));

        MockEndpoint.assertIsSatisfied(context);
        // 1 original attempt + 3 redeliveries (maximumRedeliveries(3)) = 4 calls into the gateway.
        assertEquals(4, paymentBean.getAttempts());
    }
}
