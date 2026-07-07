// SPDX-License-Identifier: Apache-2.0
package com.example.eip.enricher;

import java.util.Map;

import org.springframework.stereotype.Component;

/**
 * The "other source" the Content Enricher reads from — here a tiny in-memory customer directory.
 *
 * <p>In a real system this would be a database, a REST call or an LDAP lookup. Keeping it a plain
 * bean makes the point of the pattern clear: enrichment is just <em>calling out to fetch more data
 * and merging it in</em>. Unknown ids return {@code null} so we can show the enricher coping with a
 * miss without losing the order.
 */
@Component
public class CustomerRepository {

    private static final Map<String, Customer> DIRECTORY = Map.of(
            "C-1", new Customer("C-1", "Alice Kumar", "alice@example.com", "GOLD"),
            "C-2", new Customer("C-2", "Bruno Lang", "bruno@example.com", "SILVER"),
            "C-3", new Customer("C-3", "Chika Obi", "chika@example.com", "BRONZE"));

    /**
     * Look up a customer by id. Returns {@code null} when the id is unknown.
     * Called from the {@code direct:customer-lookup} route via Camel bean parameter binding.
     */
    public Customer findById(String customerId) {
        return DIRECTORY.get(customerId);
    }
}
