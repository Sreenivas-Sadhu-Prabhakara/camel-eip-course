// SPDX-License-Identifier: Apache-2.0
package com.example.eip.translator;

/**
 * The INCOMING (legacy) message shape — the format we do NOT control.
 *
 * <p>A partner system sends terse, abbreviated keys and money as an integer number of <b>cents</b>:
 * <pre>{ "id": "A-1001", "cust": "Acme Corp", "amt": 123499, "cur": "USD" }</pre>
 *
 * <p>The field names here match the JSON keys exactly so Jackson can bind them with no annotations.
 * This class exists only so {@code unmarshal().json(...)} has a target type; the Message Translator
 * then maps it onto our clean {@link Order} model (see {@link OrderMapper}).
 */
public class LegacyOrder {

    private String id;    // -> Order.orderId
    private String cust;  // -> Order.customer
    private long amt;     // amount in CENTS -> Order.amount (currency units)
    private String cur;   // -> Order.currency

    public LegacyOrder() {
    }

    public LegacyOrder(String id, String cust, long amt, String cur) {
        this.id = id;
        this.cust = cust;
        this.amt = amt;
        this.cur = cur;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCust() {
        return cust;
    }

    public void setCust(String cust) {
        this.cust = cust;
    }

    public long getAmt() {
        return amt;
    }

    public void setAmt(long amt) {
        this.amt = amt;
    }

    public String getCur() {
        return cur;
    }

    public void setCur(String cur) {
        this.cur = cur;
    }

    @Override
    public String toString() {
        return "LegacyOrder{id='" + id + "', cust='" + cust + "', amt=" + amt + ", cur='" + cur + "'}";
    }
}
