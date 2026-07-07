// SPDX-License-Identifier: Apache-2.0
package com.example.eip.errors;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * doTry() / doCatch() — Camel's inline equivalent of Java's {@code try/catch}.
 *
 * <p>Reach for this when you want to handle a failure RIGHT HERE, in the middle of a route, rather than
 * with a route-wide {@code onException} or {@code errorHandler}. The exception is dealt with locally by
 * {@code doCatch} and never reaches the route's error handler; after {@code end()} the route carries on
 * normally. It reads exactly like the Java you already know:
 *
 * <pre>
 * try            { charge(); settle(); }
 * catch (RuntimeException e) { compensate(); }
 * </pre>
 */
@Component
public class TryCatchRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("direct:settle")
            .routeId("try-catch-demo")
            .doTry()
                .bean("paymentBean", "process")     // may throw
                .to("{{ep.settled}}")               // reached only if the charge succeeded
            .doCatch(RuntimeException.class)
                .log("Caught locally for ${body.orderId}: ${exception.message}")
                .to("{{ep.caught}}")                // handled inline; the route continues after end()
            .end();
    }
}
