// SPDX-License-Identifier: Apache-2.0
package com.example.eip.errors;

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
 * The test IS the specification of the three error-handling strategies.
 *
 * <p>In the test profile every endpoint resolves to a {@code mock:} endpoint (see
 * {@code src/test/resources/application.yaml}), so we can assert exactly where each message ended up —
 * no broker required. Each test drives one branch:
 * <ul>
 *   <li>{@code onException + handled(true)} — good order to {@code mock:ok}; failing order diverted to
 *       {@code mock:error} with NO exception thrown back to the caller.</li>
 *   <li>{@code deadLetterChannel + redelivery} — a persistently failing order is retried then parked on
 *       {@code mock:dead}, never reaching {@code mock:charged}.</li>
 *   <li>{@code doTry/doCatch} — a failing order is caught locally to {@code mock:caught}; a good order
 *       settles to {@code mock:settled}.</li>
 * </ul>
 */
@CamelSpringBootTest
@SpringBootTest
class ErrorHandlingTest {

    @Autowired
    CamelContext context;

    @Autowired
    ProducerTemplate template;

    @EndpointInject("mock:ok")
    MockEndpoint ok;

    @EndpointInject("mock:error")
    MockEndpoint error;

    @EndpointInject("mock:dead")
    MockEndpoint dead;

    @EndpointInject("mock:charged")
    MockEndpoint charged;

    @EndpointInject("mock:settled")
    MockEndpoint settled;

    @EndpointInject("mock:caught")
    MockEndpoint caught;

    @BeforeEach
    void resetExpectations() {
        // The Spring/Camel context is shared across test methods; clear mock state between tests.
        MockEndpoint.resetMocks(context);
    }

    // --- onException + handled(true) --------------------------------------------------------------

    @Test
    void goodOrderReachesOk() throws Exception {
        ok.expectedMessageCount(1);
        error.expectedMessageCount(0);

        template.sendBody("direct:orders", new Order("A-1001", 999, false));

        MockEndpoint.assertIsSatisfied(context);
    }

    @Test
    void failingOrderIsHandledToError() throws Exception {
        ok.expectedMessageCount(0);
        error.expectedMessageCount(1);

        // handled(true) means this does NOT throw back to us even though the bean threw.
        template.sendBody("direct:orders", new Order("A-1002", 500, true));

        MockEndpoint.assertIsSatisfied(context);
    }

    // --- deadLetterChannel + redelivery -----------------------------------------------------------

    @Test
    void failingChargeIsParkedInDeadLetterAfterRetries() throws Exception {
        charged.expectedMessageCount(0);
        dead.expectedMessageCount(1);

        template.sendBody("direct:charge", new Order("A-1003", 250, true));

        MockEndpoint.assertIsSatisfied(context);
    }

    // --- doTry / doCatch ---------------------------------------------------------------------------

    @Test
    void failingSettleIsCaughtLocally() throws Exception {
        settled.expectedMessageCount(0);
        caught.expectedMessageCount(1);

        template.sendBody("direct:settle", new Order("A-1004", 250, true));

        MockEndpoint.assertIsSatisfied(context);
    }

    @Test
    void goodOrderSettlesNormally() throws Exception {
        settled.expectedMessageCount(1);
        caught.expectedMessageCount(0);

        template.sendBody("direct:settle", new Order("A-1005", 250, false));

        MockEndpoint.assertIsSatisfied(context);
    }
}
