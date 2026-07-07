// SPDX-License-Identifier: Apache-2.0
package com.example.eip.aggregator;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Aggregator (EIP): combine related messages into one — the mirror image of the Splitter.
 *
 * <p>Line items arrive one at a time on {@code direct:items}, each tagged with an {@code orderId} header
 * (the correlation key) and a {@code total} header (how many items that order will send). The Aggregator
 * groups items by {@code orderId}, folds each into an {@link OrderConfirmation} via
 * {@link LineItemAggregationStrategy}, and emits the completed confirmation once a completion condition
 * fires. The single output endpoint is a {@code {{property}}} placeholder so production can point it at a
 * real {@code direct:}/{@code jms:} endpoint while tests point it at a {@code mock:} endpoint. The numbered
 * comments below are referenced one-for-one by the lesson page's annotated walkthrough.
 */
@Component
public class AggregatorRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("direct:items")                                       // (1) each line item arrives on its own
            .routeId("aggregator")                                 //     always name a route (tracing, metrics, tests)
            .log("Item ${body.sku} for order ${header.orderId} (${header.total} expected)")
            .aggregate(header("orderId"), new LineItemAggregationStrategy()) // (2) correlate on orderId + how to merge
                .completionSize(header("total"))                   // (3) complete when all expected items are in…
                .completionTimeout(2000)                           // (4) …or after 2s of silence (the safety net)
                .log("Confirmation for order ${body.orderId}: ${body.totalAmount} total")
                .to("{{ep.out}}")                                  // (5) emit ONE aggregated confirmation
            .end();
    }
}
