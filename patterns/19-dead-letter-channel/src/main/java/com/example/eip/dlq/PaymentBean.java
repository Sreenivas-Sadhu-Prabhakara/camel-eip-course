// SPDX-License-Identifier: Apache-2.0
package com.example.eip.dlq;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

/**
 * A stand-in "payment gateway". Successful orders return quietly; flagged orders always throw a
 * {@link PaymentDeclinedException}, which is exactly the kind of permanent failure the Dead Letter
 * Channel exists to catch.
 *
 * <p>It counts how many times {@link #charge} is invoked so the test can prove that the Dead Letter
 * Channel really retried: one original attempt plus three redeliveries = four calls in total. The
 * counter is an {@link AtomicInteger} because redelivery may hop threads.
 *
 * <p>Registered as a Spring bean named {@code paymentBean}; the route references it by that name via
 * {@code .bean("paymentBean", "charge")}, so the route and the test share this one instance.
 */
@Component("paymentBean")
public class PaymentBean {

    private final AtomicInteger attempts = new AtomicInteger();

    public void charge(Order order) {
        attempts.incrementAndGet();
        if (order.isFailPayment()) {
            throw new PaymentDeclinedException("Payment gateway declined order " + order.getOrderId());
        }
        // Success: nothing to do — the order flows on to the OK channel.
    }

    /** Total number of {@link #charge} invocations since the last reset (original try + redeliveries). */
    public int getAttempts() {
        return attempts.get();
    }

    public void resetAttempts() {
        attempts.set(0);
    }
}
