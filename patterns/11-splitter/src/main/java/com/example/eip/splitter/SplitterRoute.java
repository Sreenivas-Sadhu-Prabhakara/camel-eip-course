// SPDX-License-Identifier: Apache-2.0
package com.example.eip.splitter;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Splitter (EIP): transmit ONE composite message as a SEQUENCE of smaller messages, so each element
 * can be processed on its own.
 *
 * <p>Here a single {@link Order} that carries a list of line items enters on {@code direct:orders};
 * {@code split()} fans it into one message per item, each flowing on to the {@code {{ep.item}}}
 * placeholder (a {@code log:} endpoint when you run the app, a {@code mock:} endpoint under test).
 * Camel stamps every sub-message with {@code CamelSplitIndex} (0-based position) and — because we do
 * NOT stream — {@code CamelSplitSize} (the total), which a downstream can use to track progress or
 * re-assemble. The numbered comments are referenced one-for-one by the lesson page's walkthrough.
 */
@Component
public class SplitterRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("direct:orders")                                          // (1) a bulk order arrives as ONE message
            .routeId("splitter")
            .log("Splitting order ${body.orderId}")
            .split(simple("${body.items}"))                            // (2) the Splitter: fan the list out
                // .streaming()                                        //     (see README) enable for huge inputs:
                //                                                     //     constant memory, but CamelSplitSize
                //                                                     //     is unknown until the final element
                .log("item ${header.CamelSplitIndex} of ${header.CamelSplitSize}: ${body}") // (3) per-item
                .to("{{ep.item}}")                                     // (4) each line item continues as its own message
            .end();                                                    // (5) split ends; the original exchange resumes
    }
}
