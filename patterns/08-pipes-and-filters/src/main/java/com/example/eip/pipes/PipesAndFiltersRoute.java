// SPDX-License-Identifier: Apache-2.0
package com.example.eip.pipes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Pipes and Filters (EIP): break a task into a chain of independent processing steps ("filters")
 * connected by channels ("pipes"), where each filter does exactly one thing.
 *
 * <p>A plain sequence of {@code .bean(...)} / {@code .to(...)} calls <b>is</b> Camel's default pipeline:
 * each step receives the previous step's output as its input. Here the order flows through three
 * single-responsibility filters and then out to {@code {{ep.out}}} (a {@code log:} endpoint when you run
 * the app, a {@code mock:} endpoint under test). The numbered comments are referenced one-for-one by the
 * lesson page's annotated walkthrough.
 */
@Component
public class PipesAndFiltersRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("direct:orders")                 // (1) the channel orders arrive on
            .routeId("pipeline")              //     always name a route (tracing, metrics, tests)
            .log("Pipeline in:  ${body}")
            .bean(NormalizeBean.class)        // (2) filter: trim + upper-case the country
            .bean(EnrichBean.class)           // (3) filter: stamp a receivedAt timestamp
            .bean(ValidateBean.class)         // (4) filter: reject the order if orderId is missing
            .log("Pipeline out: ${body}")
            .to("{{ep.out}}");                // (5) the pipeline's output channel
    }
}
