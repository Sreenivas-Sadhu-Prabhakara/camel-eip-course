// SPDX-License-Identifier: Apache-2.0
package com.example.eip.aggregator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

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
 * The test IS the specification of the Aggregator.
 *
 * <p>In the test profile the output endpoint resolves to {@code mock:out} (see
 * {@code src/test/resources/application.yaml}), so we can assert exactly how many confirmations were
 * emitted and what each contains — no broker required. Each item is sent with an {@code orderId} header
 * (the correlation key) and a {@code total} header (the expected item count that drives completion).
 */
@CamelSpringBootTest
@SpringBootTest
class AggregatorTest {

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

    /** The core promise: 3 items for one order become exactly ONE confirmation carrying all 3. */
    @Test
    void threeLineItemsBecomeOneConfirmation() throws Exception {
        out.expectedMessageCount(1);

        Map<String, Object> headers = headers("A-1", 3);
        template.sendBodyAndHeaders("direct:items", new LineItem("SKU-BOOK", 30), headers);
        template.sendBodyAndHeaders("direct:items", new LineItem("SKU-PEN", 5), headers);
        template.sendBodyAndHeaders("direct:items", new LineItem("SKU-BAG", 65), headers);

        // completionSize (== total header, 3) fires as soon as the third item arrives.
        out.assertIsSatisfied();

        OrderConfirmation confirmation = out.getReceivedExchanges().get(0).getIn().getBody(OrderConfirmation.class);
        assertEquals("A-1", confirmation.getOrderId());
        assertEquals(3, confirmation.getItems().size());
        assertEquals(100, confirmation.getTotalAmount());   // 30 + 5 + 65
    }

    /** Safety net: if an expected item never shows up, completionTimeout (2s) still releases the partial. */
    @Test
    void aggregatorCompletesOnTimeoutWhenAnItemIsMissing() throws Exception {
        out.expectedMessageCount(1);

        Map<String, Object> headers = headers("B-2", 3);   // promises 3 items…
        template.sendBodyAndHeaders("direct:items", new LineItem("SKU-BOOK", 30), headers);
        template.sendBodyAndHeaders("direct:items", new LineItem("SKU-PEN", 5), headers);
        // …but only 2 arrive, so completionSize is never met — the timeout is what completes the group.

        out.assertIsSatisfied();

        OrderConfirmation confirmation = out.getReceivedExchanges().get(0).getIn().getBody(OrderConfirmation.class);
        assertEquals("B-2", confirmation.getOrderId());
        assertEquals(2, confirmation.getItems().size());
        assertEquals(35, confirmation.getTotalAmount());    // 30 + 5
    }

    /** Correlation: items from two interleaved orders aggregate into two independent confirmations. */
    @Test
    void differentOrderIdsAggregateIndependently() throws Exception {
        out.expectedMessageCount(2);

        // Interleave two orders; correlation on the orderId header keeps them in separate groups.
        template.sendBodyAndHeaders("direct:items", new LineItem("SKU-A", 10), headers("C-1", 2));
        template.sendBodyAndHeaders("direct:items", new LineItem("SKU-B", 20), headers("D-9", 2));
        template.sendBodyAndHeaders("direct:items", new LineItem("SKU-C", 30), headers("C-1", 2));
        template.sendBodyAndHeaders("direct:items", new LineItem("SKU-D", 40), headers("D-9", 2));

        out.assertIsSatisfied();

        Map<String, OrderConfirmation> byOrder = new HashMap<>();
        for (Exchange ex : out.getReceivedExchanges()) {
            OrderConfirmation c = ex.getIn().getBody(OrderConfirmation.class);
            byOrder.put(c.getOrderId(), c);
        }
        assertEquals(2, byOrder.size());
        assertEquals(2, byOrder.get("C-1").getItems().size());
        assertEquals(40, byOrder.get("C-1").getTotalAmount());   // 10 + 30
        assertEquals(2, byOrder.get("D-9").getItems().size());
        assertEquals(60, byOrder.get("D-9").getTotalAmount());   // 20 + 40
    }

    /** Every item of an order carries the same correlation key and expected-count headers. */
    private static Map<String, Object> headers(String orderId, int total) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("orderId", orderId);
        headers.put("total", total);
        return headers;
    }
}
