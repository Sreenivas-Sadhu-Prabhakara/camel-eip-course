// SPDX-License-Identifier: Apache-2.0
package com.example.eip.capstone;

/** The canonical ShopFlow order. Plain getters/setters + no-arg constructor so Jackson can bind it. */
public class Order {

    private String orderId;
    private String country;
    private int totalAmount;
    private boolean testFlag;
    private boolean fraudulent;

    public Order() {
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

    public boolean isTestFlag() {
        return testFlag;
    }

    public void setTestFlag(boolean testFlag) {
        this.testFlag = testFlag;
    }

    public boolean isFraudulent() {
        return fraudulent;
    }

    public void setFraudulent(boolean fraudulent) {
        this.fraudulent = fraudulent;
    }

    @Override
    public String toString() {
        return "Order{orderId='" + orderId + "', country='" + country + "', totalAmount=" + totalAmount
                + ", testFlag=" + testFlag + ", fraudulent=" + fraudulent + "}";
    }
}
