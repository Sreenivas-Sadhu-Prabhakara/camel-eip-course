// SPDX-License-Identifier: Apache-2.0
package com.example.eip.idempotent;

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
 * The test IS the specification of the Idempotent Consumer.
 *
 * <p>In the test profile the exactly-once destination resolves to {@code mock:out}
 * (see {@code src/test/resources/application.yaml}), so we can assert exactly how many messages survived
 * deduplication — no broker required.
 *
 * <p>The idempotency key is the {@code orderId} header. Two deliveries carrying the same key are one
 * logical order (a redelivery / double-submit) and must be processed once; a different key is a genuinely
 * new order and must pass. The whole story lives in one test method because the in-memory
 * {@code IdempotentRepository} is a single instance shared by the CamelContext across test methods —
 * keeping the scenario in one method makes the "seen keys" state unambiguous.
 */
@CamelSpringBootTest
@SpringBootTest
class IdempotentConsumerTest {

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
    void duplicatesAreDroppedButDistinctIdsPassThrough() throws Exception {
        // Same orderId sent twice — the second is a duplicate and must be silently dropped.
        out.expectedMessageCount(1);

        template.sendBodyAndHeader("direct:orders", new Order("A-1001", 999), "orderId", "A-1001");
        template.sendBodyAndHeader("direct:orders", new Order("A-1001", 999), "orderId", "A-1001"); // duplicate

        out.assertIsSatisfied();   // fired EXACTLY once

        // A second, DISTINCT orderId is a genuinely new order — it passes, bringing the total to 2.
        out.expectedMessageCount(2);

        template.sendBodyAndHeader("direct:orders", new Order("A-1002", 500), "orderId", "A-1002");

        out.assertIsSatisfied();   // now 2 total
    }
}
