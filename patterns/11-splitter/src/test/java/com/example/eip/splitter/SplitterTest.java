// SPDX-License-Identifier: Apache-2.0
package com.example.eip.splitter;

import java.util.List;

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
 * The test IS the specification of the Splitter.
 *
 * <p>In the test profile the per-item endpoint resolves to {@code mock:item} (see
 * {@code src/test/resources/application.yaml}), so we can assert exactly how many messages the fan-out
 * produced and what each one carried — no broker required.
 */
@CamelSpringBootTest
@SpringBootTest
class SplitterTest {

    @Autowired
    CamelContext context;

    @Autowired
    ProducerTemplate template;

    @EndpointInject("mock:item")
    MockEndpoint item;

    @BeforeEach
    void resetExpectations() {
        // The Spring/Camel context is shared across test methods; clear mock state between tests.
        MockEndpoint.resetMocks(context);
    }

    @Test
    void splitsBulkOrderIntoOneMessagePerLineItem() throws Exception {
        // ONE order in with THREE items -> THREE messages out, one per item, in order.
        item.expectedMessageCount(3);
        item.expectedBodiesReceived("SKU-1", "SKU-2", "SKU-3");

        template.sendBody("direct:orders",
                new Order("A-1001", List.of("SKU-1", "SKU-2", "SKU-3")));

        MockEndpoint.assertIsSatisfied(context);
    }

    @Test
    void everySplitMessageCarriesItsIndexAndTheTotalSize() throws Exception {
        item.expectedMessageCount(3);
        // Camel stamps each sub-message with CamelSplitIndex (0-based position) and — because the route
        // does NOT use .streaming() — CamelSplitSize (the total). These let a downstream track progress
        // or re-assemble the pieces later (see the Aggregator pattern).
        item.allMessages().header("CamelSplitSize").isEqualTo(3);
        item.message(0).header("CamelSplitIndex").isEqualTo(0);
        item.message(1).header("CamelSplitIndex").isEqualTo(1);
        item.message(2).header("CamelSplitIndex").isEqualTo(2);

        template.sendBody("direct:orders",
                new Order("A-2002", List.of("A", "B", "C")));

        MockEndpoint.assertIsSatisfied(context);
    }
}
