// SPDX-License-Identifier: Apache-2.0
package com.example.eip.translator;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

/**
 * The Message Translator itself: a plain Spring bean that maps the legacy shape onto the canonical one.
 *
 * <p>Keeping the mapping in a bean (invoked from the route via {@code .transform().method(...)}) instead of
 * inline in the DSL keeps the route readable and the mapping unit-testable on its own. The transformation
 * does two jobs a Data Format cannot: it <b>renames</b> fields and <b>converts units</b> (cents → currency).
 */
@Component
public class OrderMapper {

    /**
     * Map one {@link LegacyOrder} to one canonical {@link Order}.
     * Camel binds the current message body (a {@code LegacyOrder} after {@code unmarshal}) to this parameter.
     */
    public Order toCanonical(LegacyOrder legacy) {
        Order order = new Order();
        order.setOrderId(legacy.getId());                          // rename: id       -> orderId
        order.setCustomer(legacy.getCust());                       // rename: cust     -> customer
        order.setCurrency(legacy.getCur());                        // rename: cur      -> currency
        order.setAmount(BigDecimal.valueOf(legacy.getAmt())        // convert: cents   -> currency units
                .movePointLeft(2));                                //          123499  -> 1234.99
        return order;
    }
}
