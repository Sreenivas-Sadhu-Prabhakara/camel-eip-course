// SPDX-License-Identifier: Apache-2.0
package com.example.eip.enricher;

/**
 * An order as it arrives: it knows WHICH customer placed it ({@code customerId}) but not WHO they are.
 *
 * <p>The three {@code customer*} fields start empty. The Content Enricher fills them in from the
 * customer-lookup resource — that is the whole point of the pattern. Plain getters/setters (not a
 * record) so Camel's Simple language can read {@code ${body.customerId}} and the
 * {@code AggregationStrategy} can mutate the order in place.
 */
public class Order {

    private String orderId;
    private String customerId;

    // --- enriched fields: null until the Content Enricher merges customer data in ---
    private String customerName;
    private String customerEmail;
    private String customerTier;

    public Order() {
    }

    public Order(String orderId, String customerId) {
        this.orderId = orderId;
        this.customerId = customerId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerTier() {
        return customerTier;
    }

    public void setCustomerTier(String customerTier) {
        this.customerTier = customerTier;
    }

    @Override
    public String toString() {
        return "Order{orderId='" + orderId + "', customerId='" + customerId
                + "', customerName='" + customerName + "', customerEmail='" + customerEmail
                + "', customerTier='" + customerTier + "'}";
    }
}
