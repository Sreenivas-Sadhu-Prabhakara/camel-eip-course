// SPDX-License-Identifier: Apache-2.0
package com.example.eip.reqreply;

/**
 * A minimal order. Plain getters (not a record) so Camel's Simple language can read
 * {@code ${body.orderId}} via the {@code getOrderId()} accessor on every supported JDK.
 */
public class Order {

    private String orderId;
    private int amount;

    public Order() {
    }

    public Order(String orderId, int amount) {
        this.orderId = orderId;
        this.amount = amount;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Order{orderId='" + orderId + "', amount=" + amount + "}";
    }
}
