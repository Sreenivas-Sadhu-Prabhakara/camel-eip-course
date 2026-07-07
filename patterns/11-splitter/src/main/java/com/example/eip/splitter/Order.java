// SPDX-License-Identifier: Apache-2.0
package com.example.eip.splitter;

import java.util.List;

/**
 * A bulk order: one order id plus a list of line items (here, SKU strings). Plain getters (not a
 * record) so Camel's Simple language can read {@code ${body.items}} via the {@code getItems()}
 * accessor on every supported JDK. The Splitter route fans {@code items} out into one message each.
 */
public class Order {

    private String orderId;
    private List<String> items;

    public Order() {
    }

    public Order(String orderId, List<String> items) {
        this.orderId = orderId;
        this.items = items;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "Order{orderId='" + orderId + "', items=" + items + "}";
    }
}
