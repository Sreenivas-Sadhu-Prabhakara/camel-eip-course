// SPDX-License-Identifier: Apache-2.0
package com.example.eip.pipes;

import java.util.Locale;

import org.apache.camel.Handler;
import org.springframework.stereotype.Component;

/**
 * Filter #1 of the pipeline — a single-responsibility step: <b>normalise the country code</b>.
 *
 * <p>It trims surrounding whitespace and upper-cases the value so every later stage (and every other
 * route in the system) can rely on a canonical form like {@code "IN"} instead of {@code " in "}.
 *
 * <p>Because this class knows nothing about Camel routing or Spring beyond {@code @Component}, it is
 * trivially reusable and unit-testable in isolation (see {@code NormalizeBeanTest}) — the core payoff
 * of Pipes and Filters. The {@link Handler @Handler} annotation tells Camel exactly which method to
 * invoke when this bean sits in a {@code .bean(...)} step, so binding is never ambiguous.
 */
@Component
public class NormalizeBean {

    @Handler
    public Order normalize(Order order) {
        if (order.getCountry() != null) {
            order.setCountry(order.getCountry().trim().toUpperCase(Locale.ROOT));
        }
        return order;
    }
}
