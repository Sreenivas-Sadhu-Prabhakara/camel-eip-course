// SPDX-License-Identifier: Apache-2.0
package com.example.eip.pipes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.apache.camel.CamelContext;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * The test IS the specification of the Pipes and Filters pipeline.
 *
 * <p>In the test profile the output channel resolves to {@code mock:out} (see
 * {@code src/test/resources/application.yaml}), so we can inspect the body the pipeline emits after all
 * three filters have run — no broker required. This proves the two outcomes of the pipeline: a well-formed
 * order comes out normalised + enriched, and a malformed one is rejected before it reaches the output.
 */
@CamelSpringBootTest
@SpringBootTest
class PipesAndFiltersTest {

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
    void pipelineNormalisesAndEnriches() throws Exception {
        out.expectedMessageCount(1);

        // Raw input: country has surrounding spaces and is lower-cased.
        template.sendBody("direct:orders", new Order("A-1001", "  in  ", 999));

        out.assertIsSatisfied();

        Order result = out.getReceivedExchanges().get(0).getIn().getBody(Order.class);
        assertEquals("IN", result.getCountry(), "NormalizeBean should trim + upper-case the country");
        assertNotNull(result.getReceivedAt(), "EnrichBean should stamp a receivedAt timestamp");
        assertEquals("A-1001", result.getOrderId(), "orderId should flow through untouched");
    }

    @Test
    void pipelineRejectsOrderWithMissingId() throws Exception {
        // ValidateBean is the last filter; it throws, so nothing should reach the output channel.
        out.expectedMessageCount(0);

        assertThrows(CamelExecutionException.class,
                () -> template.sendBody("direct:orders", new Order(null, "IN", 250)),
                "ValidateBean should reject an order with no orderId");

        out.assertIsSatisfied();
    }
}
