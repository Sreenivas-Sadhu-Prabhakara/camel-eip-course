// SPDX-License-Identifier: Apache-2.0
package com.example.eip.aggregator;

import java.util.ArrayList;
import java.util.List;

/**
 * The aggregated result: one confirmation per order, holding every {@link LineItem} that arrived for
 * that {@code orderId} plus a running total. This is the object the {@link LineItemAggregationStrategy}
 * builds up incrementally as items stream in, and the single message the Aggregator finally emits.
 */
public class OrderConfirmation {

    private String orderId;
    private final List<LineItem> items = new ArrayList<>();
    private int totalAmount;

    public OrderConfirmation() {
    }

    public OrderConfirmation(String orderId) {
        this.orderId = orderId;
    }

    /** Fold one more line item into this confirmation, keeping the running total in sync. */
    public void addItem(LineItem item) {
        items.add(item);
        totalAmount += item.getAmount();
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public List<LineItem> getItems() {
        return items;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    @Override
    public String toString() {
        return "OrderConfirmation{orderId='" + orderId + "', items=" + items.size()
                + ", totalAmount=" + totalAmount + "}";
    }
}
