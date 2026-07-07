// SPDX-License-Identifier: Apache-2.0
package com.example.eip.aggregator;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;

/**
 * The AggregationStrategy is the heart of the Aggregator EIP: Camel hands it two exchanges at a time and
 * asks "merge these." It is called once per incoming message that shares a correlation key.
 *
 * <ul>
 *   <li><b>First item</b> for an {@code orderId}: {@code oldExchange} is {@code null}. We seed a fresh
 *       {@link OrderConfirmation} (keyed by the {@code orderId} header), fold in this item, and return it —
 *       that becomes the running accumulator Camel holds for the group.</li>
 *   <li><b>Every later item</b>: {@code oldExchange} is the accumulator; we fold the new item into its
 *       confirmation and return the SAME old exchange.</li>
 * </ul>
 *
 * <p>The strategy is stateless (all state lives on the exchanges), so a single shared instance is safe.
 */
public class LineItemAggregationStrategy implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        LineItem item = newExchange.getIn().getBody(LineItem.class);

        if (oldExchange == null) {
            // First message of this correlation group — start a new confirmation.
            String orderId = newExchange.getIn().getHeader("orderId", String.class);
            OrderConfirmation confirmation = new OrderConfirmation(orderId);
            confirmation.addItem(item);
            newExchange.getIn().setBody(confirmation);
            return newExchange;
        }

        // Subsequent messages — fold into the confirmation we're already building.
        OrderConfirmation confirmation = oldExchange.getIn().getBody(OrderConfirmation.class);
        confirmation.addItem(item);
        return oldExchange;
    }
}
