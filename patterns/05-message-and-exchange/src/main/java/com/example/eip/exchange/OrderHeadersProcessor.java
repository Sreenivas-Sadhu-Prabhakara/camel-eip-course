// SPDX-License-Identifier: Apache-2.0
package com.example.eip.exchange;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

/**
 * The IMPERATIVE way to work with a message: a plain {@link Processor}.
 *
 * <p>A {@link Processor} is handed the whole {@link Exchange} — the <em>envelope</em> that Camel carries
 * through a route. The payload you almost always want lives on {@code exchange.getMessage()}: the current
 * {@link Message}, which is a <em>body + headers</em> pair. Here we read the {@code Map} body and copy two
 * of its fields up into headers so that later EIPs (routers, filters, aggregators) can make decisions on
 * that control metadata without re-parsing the body.
 *
 * <p>Prefer {@code exchange.getMessage()} over the older {@code exchange.getIn()}: it always returns the
 * message the next step will see, which keeps this code correct even after other steps swap the message.
 */
public class OrderHeadersProcessor implements Processor {

    @Override
    public void process(Exchange exchange) {
        // (1) The Exchange is the envelope; the Message is what's inside (body + headers).
        Message message = exchange.getMessage();

        // (2) getBody(Type) type-converts for you; the body here already is a Map.
        @SuppressWarnings("unchecked")
        Map<String, Object> order = message.getBody(Map.class);

        // (3) Lift two body fields into HEADERS — metadata that rides alongside the body.
        message.setHeader("orderId", order.get("orderId"));
        message.setHeader("country", order.get("country"));
    }
}
