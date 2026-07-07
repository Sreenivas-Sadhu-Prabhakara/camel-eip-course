// SPDX-License-Identifier: Apache-2.0
package com.example.eip.translator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * The test IS the specification of the Message Translator.
 *
 * <p>We push a LEGACY JSON String (terse keys, money in cents) into {@code direct:orders}. The route
 * unmarshals it to a {@link LegacyOrder}, translates it to a canonical {@link Order} via {@link OrderMapper},
 * and marshals that back to JSON. In the test profile {@code ep.out} resolves to {@code mock:out}
 * (see {@code src/test/resources/application.yaml}), so we can parse the emitted JSON and assert on the
 * renamed field and the converted amount — no broker required.
 */
@CamelSpringBootTest
@SpringBootTest
class MessageTranslatorTest {

    private static final ObjectMapper JSON = new ObjectMapper();

    // Legacy shape: id/cust/amt/cur, with amt = 123499 CENTS.
    private static final String LEGACY_JSON =
        "{ \"id\": \"A-1001\", \"cust\": \"Acme Corp\", \"amt\": 123499, \"cur\": \"USD\" }";

    @Autowired
    CamelContext context;

    @Autowired
    ProducerTemplate template;

    @EndpointInject("mock:out")
    MockEndpoint out;

    @BeforeEach
    void resetExpectations() {
        // The Spring/Camel context is shared across test methods; clear mock state between tests.
        MockEndpoint.resetMocks(context);
    }

    @Test
    void legacyJsonIsTranslatedToCanonicalJson() throws Exception {
        out.expectedMessageCount(1);

        template.sendBody("direct:orders", LEGACY_JSON);

        out.assertIsSatisfied();

        // The emitted body is canonical JSON. Parse it and assert on the meaning, not on byte order.
        String canonical = out.getExchanges().get(0).getMessage().getBody(String.class);
        JsonNode node = JSON.readTree(canonical);

        // Field renamed: cust -> customer.
        assertEquals("Acme Corp", node.get("customer").asText());
        // Field renamed: id -> orderId.
        assertEquals("A-1001", node.get("orderId").asText());
        // Field renamed: cur -> currency.
        assertEquals("USD", node.get("currency").asText());
        // Unit converted: 123499 cents -> 1234.99 currency units.
        assertEquals(0, new BigDecimal("1234.99").compareTo(node.get("amount").decimalValue()));

        // The legacy keys must be gone from the canonical output.
        assertTrue(node.get("cust") == null, "legacy key 'cust' should not survive translation");
        assertTrue(node.get("amt") == null, "legacy key 'amt' should not survive translation");
    }

    @Test
    void anotherOrderConvertsCentsCorrectly() throws Exception {
        out.expectedMessageCount(1);

        // amt = 4999 cents -> 49.99
        template.sendBody("direct:orders",
            "{ \"id\": \"A-1002\", \"cust\": \"Globex\", \"amt\": 4999, \"cur\": \"EUR\" }");

        out.assertIsSatisfied();

        JsonNode node = JSON.readTree(out.getExchanges().get(0).getMessage().getBody(String.class));
        assertEquals("Globex", node.get("customer").asText());
        assertEquals("EUR", node.get("currency").asText());
        assertEquals(0, new BigDecimal("49.99").compareTo(node.get("amount").decimalValue()));
    }
}
