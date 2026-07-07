// SPDX-License-Identifier: Apache-2.0
package com.example.eip.capstone;

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
 * End-to-end spec for the capstone pipeline. Each test uses DISTINCT orderIds because the idempotent
 * repository lives for the whole (shared) CamelContext and would otherwise carry ids across methods.
 */
@CamelSpringBootTest
@SpringBootTest
class ShopFlowPipelineTest {

    @Autowired
    CamelContext context;

    @Autowired
    ProducerTemplate template;

    @EndpointInject("mock:audit")
    MockEndpoint audit;
    @EndpointInject("mock:warehouse")
    MockEndpoint warehouse;
    @EndpointInject("mock:invoicing")
    MockEndpoint invoicing;
    @EndpointInject("mock:event")
    MockEndpoint event;
    @EndpointInject("mock:dead")
    MockEndpoint dead;

    @BeforeEach
    void reset() {
        MockEndpoint.resetMocks(context);
    }

    private static String order(String id, String country, int amount, boolean test, boolean fraud) {
        return "{\"orderId\":\"" + id + "\",\"country\":\"" + country + "\",\"totalAmount\":" + amount
                + ",\"testFlag\":" + test + ",\"fraudulent\":" + fraud + "}";
    }

    @Test
    void validDomesticOrderFlowsAllTheWay() throws Exception {
        warehouse.expectedMessageCount(1);
        invoicing.expectedMessageCount(1);
        event.expectedMessageCount(1);
        audit.expectedMessageCount(1);
        dead.expectedMessageCount(0);

        template.sendBody("direct:orders", order("OK-IN-1", "IN", 999, false, false));

        MockEndpoint.assertIsSatisfied(context);
    }

    @Test
    void testAndZeroValueOrdersAreFilteredOut() throws Exception {
        warehouse.expectedMessageCount(0);
        audit.expectedMessageCount(0);
        dead.expectedMessageCount(0);

        template.sendBody("direct:orders", order("TEST-1", "IN", 500, true, false));   // testFlag
        template.sendBody("direct:orders", order("ZERO-1", "DE", 0, false, false));    // zero value

        MockEndpoint.assertIsSatisfied(context);
    }

    @Test
    void duplicateOrderIdIsProcessedOnce() throws Exception {
        warehouse.expectedMessageCount(1);
        event.expectedMessageCount(1);

        String dup = order("DUP-1", "IN", 250, false, false);
        template.sendBody("direct:orders", dup);
        template.sendBody("direct:orders", dup);   // same orderId -> idempotent drop

        MockEndpoint.assertIsSatisfied(context);
    }

    @Test
    void declinedPaymentLandsInDeadLetter() throws Exception {
        dead.expectedMessageCount(1);
        warehouse.expectedMessageCount(0);
        event.expectedMessageCount(0);
        audit.expectedMessageCount(1);   // the wire tap happens before payment

        template.sendBody("direct:orders", order("FRAUD-1", "IN", 750, false, true));

        MockEndpoint.assertIsSatisfied(context);
    }
}
