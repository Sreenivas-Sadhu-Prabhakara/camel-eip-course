// SPDX-License-Identifier: Apache-2.0
package com.example.eip.filter;

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
 * The test IS the specification of the Message Filter.
 *
 * <p>In the test profile the output endpoint resolves to {@code mock:out} (see
 * {@code src/test/resources/application.yaml}), so we can assert exactly which orders survive the
 * filter — no broker required. A Message Filter has only ONE outcome to observe: kept or dropped.
 * Dropped messages leave no trace, so we prove them by their <em>absence</em> at {@code mock:out}.
 */
@CamelSpringBootTest
@SpringBootTest
class MessageFilterTest {

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
    void keepsRealOrdersAndDropsRejectableOnes() throws Exception {
        Order valid = new Order("A-1001", 999, false);   // real money, not a test  -> KEPT

        // Exactly one message should survive the filter, and it must be the valid order.
        out.expectedMessageCount(1);
        out.expectedBodiesReceived(valid);

        template.sendBody("direct:orders", valid);                         // KEPT
        template.sendBody("direct:orders", new Order("A-1002", 0, false)); // DROPPED (fails totalAmount > 0)
        template.sendBody("direct:orders", new Order("A-1003", 250, true)); // DROPPED (fails testFlag == false)

        MockEndpoint.assertIsSatisfied(context);
    }
}
