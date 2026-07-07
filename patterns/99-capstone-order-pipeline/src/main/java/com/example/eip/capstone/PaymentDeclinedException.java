// SPDX-License-Identifier: Apache-2.0
package com.example.eip.capstone;

/** Thrown by the payment gateway when an order cannot be authorized. */
public class PaymentDeclinedException extends RuntimeException {
    public PaymentDeclinedException(String message) {
        super(message);
    }
}
