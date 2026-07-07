// SPDX-License-Identifier: Apache-2.0
package com.example.eip.translator;

import java.math.BigDecimal;

/**
 * The CANONICAL (internal) message shape — the clean model the rest of our system speaks.
 *
 * <p>Compared with {@link LegacyOrder}: fields are renamed to readable names and money is a
 * {@link BigDecimal} in whole currency units (dollars, not cents). Jackson serialises the getters,
 * so {@code marshal().json(...)} produces:
 * <pre>{ "orderId": "A-1001", "customer": "Acme Corp", "amount": 1234.99, "currency": "USD" }</pre>
 */
public class Order {

    private String orderId;
    private String customer;
    private BigDecimal amount;
    private String currency;

    public Order() {
    }

    public Order(String orderId, String customer, BigDecimal amount, String currency) {
        this.orderId = orderId;
        this.customer = customer;
        this.amount = amount;
        this.currency = currency;
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return "Order{orderId='" + orderId + "', customer='" + customer
                + "', amount=" + amount + ", currency='" + currency + "'}";
    }
}
