// SPDX-License-Identifier: Apache-2.0
package com.example.eip.firstroute;

/**
 * A minimal order. Plain getters (not a record) so Camel's Simple language can read
 * {@code ${body.orderId}} / {@code ${body.customer}} via the {@code getOrderId()} /
 * {@code getCustomer()} accessors on every supported JDK.
 */
public class Order {

    private String orderId;
    private String customer;

    public Order() {
    }

    public Order(String orderId, String customer) {
        this.orderId = orderId;
        this.customer = customer;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    @Override
    public String toString() {
        return "Order{orderId='" + orderId + "', customer='" + customer + "'}";
    }
}
