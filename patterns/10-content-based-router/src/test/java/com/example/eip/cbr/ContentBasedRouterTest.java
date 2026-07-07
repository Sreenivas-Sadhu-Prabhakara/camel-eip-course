// SPDX-License-Identifier: Apache-2.0
package com.example.eip.cbr;

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
 * The test IS the specification of the Content-Based Router.
 *
 * <p>In the test profile the branch endpoints resolve to {@code mock:domestic}, {@code mock:eu},
 * {@code mock:international} (see {@code src/test/resources/application.yaml}), so we can assert exactly
 * which branch each order took — no broker required.
 */
@CamelSpringBootTest
@SpringBootTest
class ContentBasedRouterTest {

    @Autowired
    CamelContext context;

    @Autowired
    ProducerTemplate template;

    @EndpointInject("mock:domestic")
    MockEndpoint domestic;

    @EndpointInject("mock:eu")
    MockEndpoint eu;

    @EndpointInject("mock:international")
    MockEndpoint international;

    @BeforeEach
    void resetExpectations() {
        // The Spring/Camel context is shared across test methods; clear mock state between tests.
        MockEndpoint.resetMocks(context);
    }

    @Test
    void domesticOrderGoesDomestic() throws Exception {
        domestic.expectedMessageCount(1);
        eu.expectedMessageCount(0);
        international.expectedMessageCount(0);

        template.sendBody("direct:orders", new Order("A-1001", "IN", 999));

        MockEndpoint.assertIsSatisfied(context);
    }

    @Test
    void euOrderGoesToEuHub() throws Exception {
        domestic.expectedMessageCount(0);
        eu.expectedMessageCount(1);
        international.expectedMessageCount(0);

        template.sendBody("direct:orders", new Order("A-1002", "DE", 500));

        MockEndpoint.assertIsSatisfied(context);
    }

    @Test
    void unknownCountryGoesInternational() throws Exception {
        domestic.expectedMessageCount(0);
        eu.expectedMessageCount(0);
        international.expectedMessageCount(1);

        template.sendBody("direct:orders", new Order("A-1003", "US", 250));

        MockEndpoint.assertIsSatisfied(context);
    }
}
