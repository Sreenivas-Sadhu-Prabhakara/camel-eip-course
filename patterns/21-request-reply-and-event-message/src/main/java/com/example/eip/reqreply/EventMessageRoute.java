// SPDX-License-Identifier: Apache-2.0
package com.example.eip.reqreply;

import org.apache.camel.ExchangePattern;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Event Message (EIP): announce that something happened and move on — NO reply is expected or waited for.
 *
 * <p>This is the <b>InOnly</b> message exchange pattern (fire-and-forget). We make the one-way intent
 * explicit with {@code setExchangePattern(ExchangePattern.InOnly)} so that even if an upstream caller used
 * an InOut template, this event is published one-way. The destination is a {@code {{property}}} placeholder
 * so production can point it at a topic ({@code jms:topic:...}) while tests point it at a {@code mock:}
 * endpoint. Because there is no reply channel, the emitter is decoupled from any (0..N) event consumers.
 */
@Component
public class EventMessageRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("direct:orders")                                     // (1) the channel order events arrive on
            .routeId("emit-event")                                //     always name a route (tracing, metrics, tests)
            .setExchangePattern(ExchangePattern.InOnly)           // (2) fire-and-forget: no reply channel
            .log("Emitting order-received event for ${body.orderId} (no reply awaited)")
            .to("{{ep.event}}");                                  // (3) publish the event (mock: in tests, log:/jms: in prod)
    }
}
