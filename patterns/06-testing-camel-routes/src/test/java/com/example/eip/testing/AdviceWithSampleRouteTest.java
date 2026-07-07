// SPDX-License-Identifier: Apache-2.0
package com.example.eip.testing;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.UseAdviceWith;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Technique 4 — {@link AdviceWith}: modify a route <em>at test time</em> without touching production code.
 * Here we tap the end of the {@code sample} route by weaving in {@code to("mock:tap")}, so we can assert on
 * what flows out of the route no matter which branch it took.
 *
 * <p><b>Why a separate class?</b> {@code @UseAdviceWith} tells Camel NOT to auto-start the context, because
 * you must weave your changes into a stopped route and then start it yourself. Mixing this with the
 * auto-started {@link SampleRouteTest} would break those tests, so the two styles live apart. The flow of
 * every AdviceWith test is: (1) advise the route by id, (2) {@code context.start()}, (3) exercise + assert.
 */
@CamelSpringBootTest
@SpringBootTest
@UseAdviceWith
class AdviceWithSampleRouteTest {

    @Autowired
    CamelContext context;

    @Autowired
    ProducerTemplate template;

    @EndpointInject("mock:tap")
    MockEndpoint tap;

    @Test
    void weaveAddLastLetsUsTapTheRouteOutput() throws Exception {
        // (1) Weave an extra step onto the END of the "sample" route — runs after whichever branch fired.
        AdviceWith.adviceWith(context, "sample", rb -> rb.weaveAddLast().to("mock:tap"));

        // (2) With @UseAdviceWith the context is NOT auto-started — start it now that weaving is done.
        context.start();

        // (3) Exercise and assert. A non-VIP body takes the otherwise branch, then still hits mock:tap.
        tap.expectedBodiesReceived("weekend delivery");

        template.sendBody("direct:orders", "weekend delivery");

        tap.assertIsSatisfied();
    }
}
