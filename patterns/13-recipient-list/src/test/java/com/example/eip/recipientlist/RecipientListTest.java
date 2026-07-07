// SPDX-License-Identifier: Apache-2.0
package com.example.eip.recipientlist;

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
 * The test IS the specification of the Recipient List.
 *
 * <p>In the test profile the recipient endpoints resolve to {@code mock:warehouse}, {@code mock:invoicing},
 * {@code mock:analytics} (see {@code src/test/resources/application.yaml}), so we can assert exactly which
 * systems each order fanned out to — no broker required.
 *
 * <ul>
 *   <li>A low-value order goes to warehouse + invoicing only (analytics is skipped).</li>
 *   <li>A high-value order goes to all three (analytics is added dynamically).</li>
 * </ul>
 */
@CamelSpringBootTest
@SpringBootTest
class RecipientListTest {

    @Autowired
    CamelContext context;

    @Autowired
    ProducerTemplate template;

    @EndpointInject("mock:warehouse")
    MockEndpoint warehouse;

    @EndpointInject("mock:invoicing")
    MockEndpoint invoicing;

    @EndpointInject("mock:analytics")
    MockEndpoint analytics;

    @BeforeEach
    void resetExpectations() {
        // The Spring/Camel context is shared across test methods; clear mock state between tests.
        MockEndpoint.resetMocks(context);
    }

    @Test
    void lowValueOrderSkipsAnalytics() throws Exception {
        warehouse.expectedMessageCount(1);
        invoicing.expectedMessageCount(1);
        analytics.expectedMessageCount(0);

        // 250 is below the high-value threshold (1000): warehouse + invoicing only.
        template.sendBody("direct:orders", new Order("A-2001", "IN", 250));

        MockEndpoint.assertIsSatisfied(context);
    }

    @Test
    void highValueOrderAlsoHitsAnalytics() throws Exception {
        warehouse.expectedMessageCount(1);
        invoicing.expectedMessageCount(1);
        analytics.expectedMessageCount(1);

        // 5000 is at/above the high-value threshold (1000): analytics is added to the recipient list.
        template.sendBody("direct:orders", new Order("A-2002", "IN", 5000));

        MockEndpoint.assertIsSatisfied(context);
    }
}
