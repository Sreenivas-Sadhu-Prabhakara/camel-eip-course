// SPDX-License-Identifier: Apache-2.0
package com.example.eip.cbr;

/**
 * A minimal order. Plain getters (not a record) so Camel's Simple language can read
 * {@code ${body.country}} via the {@code getCountry()} accessor on every supported JDK.
 */
public class Order {

    private String orderId;
    private String country;
    private int totalAmount;

    public Order() {
    }

    public Order(String orderId, String country, int totalAmount) {
        this.orderId = orderId;
        this.country = country;
        this.totalAmount = totalAmount;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Override
    public String toString() {
        return "Order{orderId='" + orderId + "', country='" + country + "', totalAmount=" + totalAmount + "}";
    }
}
