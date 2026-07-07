// SPDX-License-Identifier: Apache-2.0
package com.example.eip.dlq;

/**
 * A minimal order. Plain getters (not a record) so Camel's Simple language can read
 * {@code ${body.orderId}} via the {@code getOrderId()} accessor on every supported JDK.
 *
 * <p>The {@code failPayment} flag is the demo's way of saying "this order's card will always be
 * declined" — the {@link PaymentBean} throws for it every time, which is exactly the permanent
 * failure the Dead Letter Channel is there to catch.
 */
public class Order {

    private String orderId;
    private int amount;
    private boolean failPayment;

    public Order() {
    }

    public Order(String orderId, int amount, boolean failPayment) {
        this.orderId = orderId;
        this.amount = amount;
        this.failPayment = failPayment;
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

    public boolean isFailPayment() {
        return failPayment;
    }

    public void setFailPayment(boolean failPayment) {
        this.failPayment = failPayment;
    }

    @Override
    public String toString() {
        return "Order{orderId='" + orderId + "', amount=" + amount + ", failPayment=" + failPayment + "}";
    }
}
