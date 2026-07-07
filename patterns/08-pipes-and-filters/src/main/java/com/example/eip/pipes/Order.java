// SPDX-License-Identifier: Apache-2.0
package com.example.eip.pipes;

/**
 * A minimal order that flows through the pipeline. Plain getters/setters (not a record) so each filter
 * can read and mutate it, and so Camel's Simple language can reach {@code ${body.country}} on every JDK.
 *
 * <p>{@code receivedAt} starts null; the {@link EnrichBean} filter fills it in mid-pipeline — that is the
 * observable proof that enrichment ran.
 */
public class Order {

    private String orderId;
    private String country;
    private int totalAmount;
    private String receivedAt;

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

    public String getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(String receivedAt) {
        this.receivedAt = receivedAt;
    }

    @Override
    public String toString() {
        return "Order{orderId='" + orderId + "', country='" + country
                + "', totalAmount=" + totalAmount + ", receivedAt='" + receivedAt + "'}";
    }
}
