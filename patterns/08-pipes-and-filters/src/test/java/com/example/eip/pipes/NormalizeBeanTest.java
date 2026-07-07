// SPDX-License-Identifier: Apache-2.0
package com.example.eip.pipes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/**
 * The reusable-filter payoff, made concrete: because {@link NormalizeBean} is a plain single-responsibility
 * class that knows nothing about Camel or Spring, we can unit-test it directly — no CamelContext, no
 * {@code @SpringBootTest}, no broker — in microseconds. That same property is exactly why the filter can be
 * dropped into any pipeline and reused.
 */
class NormalizeBeanTest {

    private final NormalizeBean bean = new NormalizeBean();

    @Test
    void trimsAndUpperCasesCountry() {
        Order out = bean.normalize(new Order("A-1", "  de ", 10));
        assertEquals("DE", out.getCountry());
    }

    @Test
    void leavesNullCountryAlone() {
        Order out = bean.normalize(new Order("A-2", null, 10));
        assertNull(out.getCountry(), "a missing country must not blow up the filter");
    }
}
