// SPDX-License-Identifier: Apache-2.0
package com.example.eip.pipes;

import java.time.Instant;

import org.apache.camel.Handler;
import org.springframework.stereotype.Component;

/**
 * Filter #2 of the pipeline — a single-responsibility step: <b>enrich the order with a receipt time</b>.
 *
 * <p>It stamps a {@code receivedAt} timestamp onto the order. This is the classic "add data the message
 * didn't arrive with" job. Here we set a field on the body; in other pipelines the same idea is a Camel
 * message header — either way it is one small, replaceable filter that does exactly one thing.
 */
@Component
public class EnrichBean {

    @Handler
    public Order enrich(Order order) {
        order.setReceivedAt(Instant.now().toString());
        return order;
    }
}
