// SPDX-License-Identifier: Apache-2.0
package com.example.eip.exchange;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * The Message and the Exchange — the two objects every Camel route is built around.
 *
 * <ul>
 *   <li><b>Exchange</b> = the envelope Camel carries down the route (ids, properties, the current Message).</li>
 *   <li><b>Message</b> = {@code exchange.getMessage()} — a <b>body</b> (the payload) plus <b>headers</b>
 *       (control metadata that later EIPs route/filter/aggregate on).</li>
 * </ul>
 *
 * <p>This route sets the SAME idea two ways so you can compare the styles:
 * <ol>
 *   <li><b>Imperative</b> — a {@link OrderHeadersProcessor} reaches into {@code exchange.getMessage()} and
 *       copies {@code orderId} + {@code country} from the {@code Map} body into headers.</li>
 *   <li><b>Declarative</b> — the DSL's {@code setHeader(...)} with the <b>Simple</b> language reading a map
 *       key ({@code ${body[amount]}}) sets {@code amount} without any Java.</li>
 * </ol>
 * The numbered comments are referenced one-for-one by the lesson page's annotated walkthrough.
 */
@Component
public class MessageAndExchangeRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("direct:orders")                                         // (1) orders arrive as a java.util.Map body
            .routeId("exchange")                                      //     always name a route (tracing, metrics, tests)
            .log("IN  exchange ${exchangeId}: the Message body (a Map) = ${body}")

            // (2) IMPERATIVE: a Processor reads exchange.getMessage() and copies fields into headers.
            .process(new OrderHeadersProcessor())

            // (3) DECLARATIVE equivalent: setHeader + the Simple language reading a Map key ${body[key]}.
            //     Same job, no Java — you could set orderId/country this way too, e.g.
            //     .setHeader("orderId", simple("${body[orderId]}")).
            .setHeader("amount", simple("${body[amount]}"))

            // (4) Headers now carry the control metadata later EIPs will route on. Log BOTH.
            .log("OUT headers (Processor):   orderId=${header.orderId}, country=${header.country}")
            .log("OUT header  (setHeader):   amount=${header.amount}")

            .to("{{ep.out}}");                                        // (5) placeholder: log: when run, mock: in tests
    }
}
