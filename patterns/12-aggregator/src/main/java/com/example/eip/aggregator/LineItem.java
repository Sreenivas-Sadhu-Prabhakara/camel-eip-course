// SPDX-License-Identifier: Apache-2.0
package com.example.eip.aggregator;

/**
 * One line item of an order — the kind of small, independent message a Splitter emits and an
 * Aggregator later re-assembles. Plain getters (not a record) so Camel's Simple language can read
 * {@code ${body.sku}} via the {@code getSku()} accessor on every supported JDK.
 *
 * <p>Note: the item does NOT carry the order id in its body — that travels as the {@code orderId}
 * message header, which is what the Aggregator correlates on.
 */
public class LineItem {

    private String sku;
    private int amount;

    public LineItem() {
    }

    public LineItem(String sku, int amount) {
        this.sku = sku;
        this.amount = amount;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "LineItem{sku='" + sku + "', amount=" + amount + "}";
    }
}
