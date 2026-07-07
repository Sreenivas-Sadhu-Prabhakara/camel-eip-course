// SPDX-License-Identifier: Apache-2.0
package com.example.eip.wiretap;

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
 * The test IS the specification of the Wire Tap.
 *
 * <p>In the test profile the two endpoints resolve to {@code mock:main} and {@code mock:audit} (see
 * {@code src/test/resources/application.yaml}), so we can assert that every order reaches the main flow
 * AND that a copy reaches the audit trail — no broker required.
 *
 * <p>The tap is <b>asynchronous</b> (fire-and-forget), but that is fine for the assertions: a
 * {@link MockEndpoint} with {@code expectedMessageCount} set already <b>waits</b> up to its timeout for
 * the expected messages to arrive, so {@code assertIsSatisfied} transparently handles the async copy.
 */
@CamelSpringBootTest
@SpringBootTest
class WireTapTest {

    @Autowired
    CamelContext context;

    @Autowired
    ProducerTemplate template;

    @EndpointInject("mock:main")
    MockEndpoint main;

    @EndpointInject("mock:audit")
    MockEndpoint audit;

    @BeforeEach
    void resetExpectations() {
        // The Spring/Camel context is shared across test methods; clear mock state between tests.
        MockEndpoint.resetMocks(context);
    }

    @Test
    void everyOrderIsCopiedToAudit() throws Exception {
        Order order = new Order("A-1001", "IN", 999);

        // Both the main flow and the audit trail must see exactly this one order.
        main.expectedMessageCount(1);
        main.expectedBodiesReceived(order);
        audit.expectedMessageCount(1);
        audit.expectedBodiesReceived(order);

        template.sendBody("direct:orders", order);

        // Waits for the async tap; passes only if BOTH mocks received the order.
        MockEndpoint.assertIsSatisfied(context);
    }

    @Test
    void tapsEverySingleOrder() throws Exception {
        // Prove it copies EVERY order, not just the first: main and audit counts must stay in lock-step.
        main.expectedMessageCount(3);
        audit.expectedMessageCount(3);

        template.sendBody("direct:orders", new Order("A-1001", "IN", 100));
        template.sendBody("direct:orders", new Order("A-1002", "DE", 200));
        template.sendBody("direct:orders", new Order("A-1003", "US", 300));

        MockEndpoint.assertIsSatisfied(context);
    }
}
