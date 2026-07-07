// SPDX-License-Identifier: Apache-2.0
package com.example.eip.errors;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Error Handling Basics: what happens when a step in a route throws.
 *
 * <p><b>Default behaviour.</b> Out of the box, Camel wraps every route in a "default error handler". If a
 * step throws and nothing else catches it, that handler logs the exception and propagates it back to the
 * caller — the exchange is marked failed and the remaining steps are skipped. Nothing is retried and
 * nothing is diverted; the error just surfaces to whoever sent the message.
 *
 * <p><b>The upgrade shown here.</b> An {@code onException(...)} clause catches a chosen exception, marks it
 * {@code handled(true)} (so the CALLER sees success, not a stack trace), and diverts the failed message to
 * an error channel instead of aborting. This is the everyday "catch, log, send it somewhere sensible" move.
 *
 * <p>The message flows through {@link PaymentBean}, which throws for any order flagged {@code failPayment}.
 * A good order continues to {@code {{ep.ok}}}; a failing one is diverted to {@code {{ep.error}}}. Both are
 * {@code {{property}}} placeholders — {@code log:} when you run the app, {@code mock:} in the tests. The
 * numbered comments are referenced one-for-one by the lesson page's annotated walkthrough.
 */
@Component
public class ErrorHandlingRoute extends RouteBuilder {

    @Override
    public void configure() {
        // (1) onException applies to EVERY route defined in THIS RouteBuilder. It catches the listed
        //     exception (and its subclasses) thrown anywhere downstream — here, from the PaymentBean.
        onException(RuntimeException.class)
            .handled(true)                                    // (2) "I've dealt with it": do NOT rethrow to the caller
            .log("Payment FAILED for ${body.orderId}: ${exception.message} -> error channel")
            .to("{{ep.error}}");                              // (3) divert the bad message to the error destination

        from("direct:orders")                                 // (4) orders arrive on this channel
            .routeId("error-handling")                        //     always name a route (tracing, metrics, tests)
            .log("Charging order ${body.orderId}")
            .bean("paymentBean", "process")                   // (5) may throw RuntimeException -> caught by (1)
            .log("Payment OK for ${body.orderId}")
            .to("{{ep.ok}}");                                 // (6) happy path: only reached if (5) did NOT throw
    }
}
