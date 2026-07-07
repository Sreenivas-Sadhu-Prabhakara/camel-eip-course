// SPDX-License-Identifier: Apache-2.0
package com.example.eip.pipes;

import org.apache.camel.Handler;
import org.springframework.stereotype.Component;

/**
 * Filter #3 of the pipeline — a single-responsibility step: <b>validate that required fields are present</b>.
 *
 * <p>It fails fast if the order has no {@code orderId}. Throwing here stops the message before it reaches
 * {@code {{ep.out}}}, so the caller (or an error handler) learns the order was rejected. Keeping validation
 * as its own filter means you can drop it into any pipeline — or leave it out — without touching the other
 * stages.
 */
@Component
public class ValidateBean {

    @Handler
    public Order validate(Order order) {
        if (order.getOrderId() == null || order.getOrderId().isBlank()) {
            throw new IllegalArgumentException("orderId is required");
        }
        return order;
    }
}
