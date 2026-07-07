// SPDX-License-Identifier: Apache-2.0
package com.example.eip.errors;

import org.springframework.stereotype.Component;

/**
 * A tiny "charge the card" step. It is a plain Spring {@code @Component} (registered as {@code paymentBean})
 * that Camel calls via {@code .bean("paymentBean", "process")}.
 *
 * <p>The method returns {@code void}, so on success Camel leaves the message body unchanged and the
 * {@link Order} simply flows on to the next step. Orders flagged {@code failPayment=true} throw a
 * {@link RuntimeException} — this is how we simulate a step failing at runtime (a declined card, a
 * downstream 500, a bug). Crucially, this bean does NOT decide what happens next: that is the job of the
 * route's error handling ({@link ErrorHandlingRoute}, {@link DeadLetterChannelRoute}, {@link TryCatchRoute}).
 */
@Component("paymentBean")
public class PaymentBean {

    public void process(Order order) {
        if (order.isFailPayment()) {
            throw new RuntimeException("card declined for order " + order.getOrderId());
        }
        // success: nothing to change — the Order continues down the route unchanged
    }
}
