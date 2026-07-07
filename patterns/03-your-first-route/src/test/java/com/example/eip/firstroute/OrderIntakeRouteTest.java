// SPDX-License-Identifier: Apache-2.0
package com.example.eip.firstroute;

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
 * The test IS the specification of your first route.
 *
 * <p>In the test profile the producer endpoint {@code {{ep.out}}} resolves to {@code mock:out}
 * (see {@code src/test/resources/application.yaml}), so we can assert exactly what the route emitted —
 * no broker required. We send one {@link Order} into the {@code from("direct:orders")} consumer and prove
 * that exactly one message reaches the producer, unchanged.
 */
@CamelSpringBootTest
@SpringBootTest
class OrderIntakeRouteTest {

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
    void oneOrderInOneMessageOut() throws Exception {
        out.expectedMessageCount(1);

        template.sendBody("direct:orders", new Order("A-1001", "Asha"));

        MockEndpoint.assertIsSatisfied(context);
    }

    @Test
    void bodyPassesThroughUnchanged() throws Exception {
        Order order = new Order("A-1002", "Bruno");
        out.expectedBodiesReceived(order);

        template.sendBody("direct:orders", order);

        MockEndpoint.assertIsSatisfied(context);
    }
}
