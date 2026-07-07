// SPDX-License-Identifier: Apache-2.0
package com.example.eip.dlq;

/**
 * Thrown by {@link PaymentBean} when a card is declined. A plain unchecked exception — Camel's error
 * handlers work on any {@link Throwable}, so nothing special is needed to make it "retryable."
 */
public class PaymentDeclinedException extends RuntimeException {

    public PaymentDeclinedException(String message) {
        super(message);
    }
}
