// SPDX-License-Identifier: Apache-2.0
package com.example.eip.enricher;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;

/**
 * The merge half of the Content Enricher.
 *
 * <p>{@code enrich()} calls this with two exchanges:
 * <ul>
 *   <li>{@code original} — the incoming {@link Order} (this is the message that keeps flowing), and</li>
 *   <li>{@code resource} — the reply from {@code direct:customer-lookup}, whose body is the {@link Customer}
 *       (or {@code null} for an unknown id).</li>
 * </ul>
 * We copy the looked-up customer's fields onto the order and return the <em>original</em> exchange, so the
 * enriched order — not the customer record — is what continues down the route. If the customer was not
 * found we simply return the order untouched, so a lookup miss never drops the message.
 */
public class MergeStrategy implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange original, Exchange resource) {
        if (resource == null) {
            return original;   // nothing came back from the resource route
        }

        Order order = original.getIn().getBody(Order.class);
        Customer customer = resource.getMessage().getBody(Customer.class);

        if (order != null && customer != null) {
            order.setCustomerName(customer.getName());
            order.setCustomerEmail(customer.getEmail());
            order.setCustomerTier(customer.getTier());
        }
        return original;
    }
}
