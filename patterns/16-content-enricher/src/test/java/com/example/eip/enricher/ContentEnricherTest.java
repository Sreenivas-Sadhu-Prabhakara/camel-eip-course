// SPDX-License-Identifier: Apache-2.0
package com.example.eip.enricher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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
 * The test IS the specification of the Content Enricher.
 *
 * <p>We send an order that only carries a {@code customerId}. After {@code enrich()} calls the
 * customer-lookup route and {@link MergeStrategy} merges the result, the order arriving on
 * {@code mock:out} must now carry the customer's name, email and tier — proving the message was
 * augmented from a second source with no broker and no HTTP.
 */
@CamelSpringBootTest
@SpringBootTest
class ContentEnricherTest {

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
    void orderIsEnrichedWithCustomerData() throws Exception {
        out.expectedMessageCount(1);

        template.sendBody("direct:orders", new Order("A-1001", "C-1"));

        out.assertIsSatisfied();

        Order enriched = out.getExchanges().get(0).getIn().getBody(Order.class);
        assertEquals("A-1001", enriched.getOrderId());
        assertEquals("C-1", enriched.getCustomerId());
        // These three fields were empty on the way in and filled by the Content Enricher:
        assertEquals("Alice Kumar", enriched.getCustomerName());
        assertEquals("alice@example.com", enriched.getCustomerEmail());
        assertEquals("GOLD", enriched.getCustomerTier());
    }

    @Test
    void enrichmentLooksUpByIdNotAConstant() throws Exception {
        out.expectedMessageCount(1);

        template.sendBody("direct:orders", new Order("A-1002", "C-2"));

        out.assertIsSatisfied();

        Order enriched = out.getExchanges().get(0).getIn().getBody(Order.class);
        // A different id yields a different customer — the lookup really keys off customerId.
        assertEquals("Bruno Lang", enriched.getCustomerName());
        assertEquals("bruno@example.com", enriched.getCustomerEmail());
        assertEquals("SILVER", enriched.getCustomerTier());
    }

    @Test
    void unknownCustomerStillFlowsThroughWithoutEnrichment() throws Exception {
        out.expectedMessageCount(1);

        template.sendBody("direct:orders", new Order("A-1003", "C-9"));

        out.assertIsSatisfied();

        Order enriched = out.getExchanges().get(0).getIn().getBody(Order.class);
        // Lookup miss: the order is NOT dropped, it just carries no customer details.
        assertEquals("A-1003", enriched.getOrderId());
        assertNull(enriched.getCustomerName());
        assertNull(enriched.getCustomerEmail());
        assertNull(enriched.getCustomerTier());
    }
}
