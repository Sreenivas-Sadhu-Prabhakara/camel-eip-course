// SPDX-License-Identifier: Apache-2.0
package com.example.eip.recipientlist;

import org.apache.camel.Body;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Computes the Recipient List at runtime.
 *
 * <p>The Recipient List EIP fans one message out to a set of destinations that is decided
 * <em>per message</em> — unlike a static {@code multicast()} whose targets are fixed at design time.
 * This bean returns a {@code ","}-separated string of endpoint URIs; the route then splits it and
 * delivers a copy to each.
 *
 * <p>The base endpoints are injected from {@code ep.*} property placeholders, so the SAME logic points
 * at real {@code jms:}/{@code http:} systems in production and at {@code mock:} endpoints under test —
 * the recipient computation never changes, only where the names resolve to.
 *
 * <ul>
 *   <li><b>warehouse</b> and <b>invoicing</b> — always notified (every order ships and bills).</li>
 *   <li><b>analytics</b> — added ONLY for high-value orders (at or above the configured threshold).</li>
 * </ul>
 */
@Component
public class RecipientResolver {

    private final String warehouse;
    private final String invoicing;
    private final String analytics;
    private final int highValueThreshold;

    public RecipientResolver(
            @Value("${ep.warehouse}") String warehouse,
            @Value("${ep.invoicing}") String invoicing,
            @Value("${ep.analytics}") String analytics,
            @Value("${app.order.high-value-threshold:1000}") int highValueThreshold) {
        this.warehouse = warehouse;
        this.invoicing = invoicing;
        this.analytics = analytics;
        this.highValueThreshold = highValueThreshold;
    }

    /**
     * Returns the comma-separated recipient URIs for this order. Camel calls this via
     * {@code method(RecipientResolver.class, "recipientsFor")} and binds the message body to {@code order}.
     */
    public String recipientsFor(@Body Order order) {
        // Always: warehouse + invoicing.
        StringBuilder recipients = new StringBuilder();
        recipients.append(warehouse).append(',').append(invoicing);

        // Conditionally add analytics for high-value orders — this is what makes the list *dynamic*.
        if (order.getTotalAmount() >= highValueThreshold) {
            recipients.append(',').append(analytics);
        }
        return recipients.toString();
    }
}
