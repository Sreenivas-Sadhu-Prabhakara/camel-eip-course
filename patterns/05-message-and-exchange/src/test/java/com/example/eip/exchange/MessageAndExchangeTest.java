// SPDX-License-Identifier: Apache-2.0
package com.example.eip.exchange;

import java.util.LinkedHashMap;
import java.util.Map;

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
 * The test IS the specification for "the Message and the Exchange".
 *
 * <p>We send a {@code Map} body into {@code direct:orders} and assert that the message landing on
 * {@code mock:out} carries the HEADERS we lifted out of the body — {@code orderId} + {@code country}
 * (set imperatively by {@link OrderHeadersProcessor}) and {@code amount} (set declaratively by the DSL's
 * {@code setHeader} + Simple). In the test profile {@code ep.out} resolves to {@code mock:out}
 * (see {@code src/test/resources/application.yaml}), so no broker is required.
 */
@CamelSpringBootTest
@SpringBootTest
class MessageAndExchangeTest {

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
    void copiesOrderIdAndCountryIntoHeaders() throws Exception {
        out.expectedMessageCount(1);
        // Imperative Processor lifted these two body fields into headers:
        out.expectedHeaderReceived("orderId", "A-2001");
        out.expectedHeaderReceived("country", "IN");
        // Declarative setHeader + Simple ${body[amount]} set this one:
        out.expectedHeaderReceived("amount", 4200);

        Map<String, Object> order = new LinkedHashMap<>();
        order.put("orderId", "A-2001");
        order.put("country", "IN");
        order.put("amount", 4200);
        template.sendBody("direct:orders", order);

        out.assertIsSatisfied();
    }

    @Test
    void headersAreDerivedFromEachMessageNotHardcoded() throws Exception {
        out.expectedMessageCount(1);
        out.expectedHeaderReceived("orderId", "A-2002");
        out.expectedHeaderReceived("country", "DE");
        out.expectedHeaderReceived("amount", 75);

        Map<String, Object> order = new LinkedHashMap<>();
        order.put("orderId", "A-2002");
        order.put("country", "DE");
        order.put("amount", 75);
        template.sendBody("direct:orders", order);

        out.assertIsSatisfied();
    }
}
