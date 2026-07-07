// SPDX-License-Identifier: Apache-2.0
package com.example.eip.translator;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

/**
 * Message Translator (EIP): convert a message from one format to another so systems that speak different
 * "languages" can integrate. Here we bridge a partner's LEGACY JSON shape to our CANONICAL {@link Order}.
 *
 * <p>Two building blocks do the work:
 * <ul>
 *   <li>a <b>Data Format</b> — the reusable codec ({@code JsonLibrary.Jackson}) plugged into
 *       {@code unmarshal}/{@code marshal} to go bytes/String &lt;-&gt; POJO; and</li>
 *   <li>the <b>Message Translator</b> proper — {@link OrderMapper}, invoked via
 *       {@code .transform().method(...)}, which renames fields and converts cents to currency units.</li>
 * </ul>
 * The out endpoint is a {@code {{property}}} placeholder so production can point it at a real
 * {@code direct:}/{@code jms:} endpoint while tests point it at a {@code mock:} endpoint. The numbered
 * comments below are referenced one-for-one by the lesson page's annotated walkthrough.
 */
@Component
public class MessageTranslatorRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("direct:orders")                                         // (1) legacy JSON arrives as a String
            .routeId("translator")                                    //     always name a route
            .log("Translating legacy order: ${body}")
            .unmarshal().json(JsonLibrary.Jackson, LegacyOrder.class)  // (2) Data Format: JSON String -> LegacyOrder
            .transform().method(OrderMapper.class, "toCanonical")     // (3) Message Translator: LegacyOrder -> Order
            .marshal().json(JsonLibrary.Jackson)                      // (4) Data Format: Order -> canonical JSON
            .log("Emitting canonical order: ${body}")
            .to("{{ep.out}}");                                        // (5) downstream (log: in run, mock: in test)
    }
}
