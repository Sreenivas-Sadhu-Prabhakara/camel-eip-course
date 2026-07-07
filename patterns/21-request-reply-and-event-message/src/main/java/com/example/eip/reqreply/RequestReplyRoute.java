// SPDX-License-Identifier: Apache-2.0
package com.example.eip.reqreply;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Request-Reply (EIP): the caller sends a message and BLOCKS until a correlated reply comes back.
 *
 * <p>This is the <b>InOut</b> message exchange pattern. The route below is a <i>responder</i>: it takes an
 * {@link Order} on {@code direct:authorize} and replaces the body with an authorization code derived from
 * the order. A caller obtains that reply synchronously with
 * {@code producerTemplate.requestBody("direct:authorize", order)} — {@code requestBody} is the InOut verb
 * (contrast with {@code sendBody}, which is one-way InOnly). Camel handles the reply channel and reply
 * correlation for you; over a real broker you would attach a Correlation Identifier so the caller can match
 * the reply to its request (see the README).
 */
@Component
public class RequestReplyRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("direct:authorize")                                   // (1) the request channel
            .routeId("payment")                                    //     always name a route (tracing, metrics, tests)
            .log("Authorizing order ${body.orderId} for amount ${body.amount}")
            .setBody(simple("AUTH-${body.orderId}"));              // (2) the reply: an auth code derived from the order
                                                                   //     requestBody(...) returns THIS as the reply
    }
}
