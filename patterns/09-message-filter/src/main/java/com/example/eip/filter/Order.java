// SPDX-License-Identifier: Apache-2.0
package com.example.eip.filter;

/**
 * A minimal order. Plain getters (not a record) so Camel's Simple language can read
 * {@code ${body.totalAmount}} and {@code ${body.testFlag}} via the standard bean accessors on
 * every supported JDK. Note the boolean field uses the {@code isTestFlag()} accessor, which
 * Camel's Simple language resolves from {@code ${body.testFlag}}.
 */
public class Order {

    private String orderId;
    private int totalAmount;
    private boolean testFlag;

    public Order() {
    }

    public Order(String orderId, int totalAmount, boolean testFlag) {
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.testFlag = testFlag;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public boolean isTestFlag() {
        return testFlag;
    }

    public void setTestFlag(boolean testFlag) {
        this.testFlag = testFlag;
    }

    @Override
    public String toString() {
        return "Order{orderId='" + orderId + "', totalAmount=" + totalAmount + ", testFlag=" + testFlag + "}";
    }
}
