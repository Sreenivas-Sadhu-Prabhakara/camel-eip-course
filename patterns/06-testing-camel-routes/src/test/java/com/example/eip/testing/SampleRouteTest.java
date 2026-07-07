// SPDX-License-Identifier: Apache-2.0
package com.example.eip.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.TimeUnit;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * The Camel test toolkit, one technique per method. Read these tests as the spec for {@link SampleRoute}.
 *
 * <p>This class uses the NORMAL (auto-started) context: {@code @CamelSpringBootTest} boots the
 * {@code CamelContext} and starts every route before each test. The {@code AdviceWith} demo needs a
 * NOT-yet-started context, so it lives in its own class ({@link AdviceWithSampleRouteTest}) to avoid a
 * conflict. In the test profile {@code {{ep.a}}}/{@code {{ep.b}}} resolve to {@code mock:priority} and
 * {@code mock:standard} (see {@code src/test/resources/application.yaml}).
 */
@CamelSpringBootTest
@SpringBootTest
class SampleRouteTest {

    @Autowired
    CamelContext context;

    @Autowired
    ProducerTemplate template;

    @EndpointInject("mock:priority")
    MockEndpoint priority;

    @EndpointInject("mock:standard")
    MockEndpoint standard;

    @BeforeEach
    void resetExpectations() {
        // The Spring/Camel context is shared across test methods; clear mock state between tests.
        MockEndpoint.resetMocks(context);
    }

    /**
     * Technique 1 — {@link MockEndpoint}. A mock: endpoint records every message it receives so you can
     * state up-front what SHOULD arrive ({@code expectedBodiesReceived} also pins the exact count) and
     * then verify it with {@code assertIsSatisfied}.
     */
    @Test
    void vipBodyReachesPriorityWithExpectedBody() throws Exception {
        priority.expectedBodiesReceived("VIP order for alice");
        standard.expectedMessageCount(0);

        template.sendBody("direct:orders", "VIP order for alice");

        MockEndpoint.assertIsSatisfied(context);
    }

    /**
     * Technique 1 (other branch) — the {@code otherwise} path. Same MockEndpoint API, proving a non-VIP
     * body lands on {@code mock:standard} and NOT on {@code mock:priority}.
     */
    @Test
    void plainBodyReachesStandard() throws Exception {
        priority.expectedMessageCount(0);
        standard.expectedBodiesReceived("weekly groceries");

        template.sendBody("direct:orders", "weekly groceries");

        MockEndpoint.assertIsSatisfied(context);
    }

    /**
     * Technique 2 — {@link ProducerTemplate#requestBody} for InOut (request/reply). Unlike
     * {@code sendBody} (fire-and-forget), {@code requestBody} sets the exchange to InOut and returns the
     * reply body, so we can assert on what the {@code echo} route sent back.
     */
    @Test
    void echoRouteRepliesWithTransformedBody() {
        String reply = template.requestBody("direct:echo", "hello", String.class);

        assertEquals("echo:hello", reply);
    }

    /**
     * Technique 3 — {@link NotifyBuilder}. It builds a condition over route activity ("one exchange
     * originating from direct:orders has finished") and blocks up to a timeout until it becomes true.
     * Priceless for asynchronous routes (seda:, wireTap, jms:) where the producer call returns before
     * processing completes. Here the route is synchronous, so the condition is already met — the point is
     * the API.
     */
    @Test
    void notifyBuilderWaitsForCompletion() {
        NotifyBuilder notify = new NotifyBuilder(context)
            .from("direct:orders")
            .whenDone(1)
            .create();

        template.sendBody("direct:orders", "VIP async-ish");

        boolean done = notify.matches(5, TimeUnit.SECONDS);
        assertTrue(done, "expected exactly one exchange from direct:orders to complete");
    }
}
