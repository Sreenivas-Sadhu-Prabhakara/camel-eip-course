// SPDX-License-Identifier: Apache-2.0
package com.example.eip.errors;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Dead Letter Channel (DLC) with a redelivery policy — the OTHER common answer to "a step threw."
 *
 * <p><b>Logging vs. a Dead Letter Channel.</b> {@link ErrorHandlingRoute} catches the exception, logs it
 * and moves on — the bad message is effectively gone once the log line scrolls past. A Dead Letter Channel
 * instead (a) RETRIES the failing step a few times in case the fault was transient, and (b) if it still
 * fails, PARKS the message on a dedicated "dead letter" destination where ops can inspect and replay it.
 * Logging tells you something broke; a Dead Letter Channel keeps the failed message safe and out of the way.
 *
 * <p>{@code errorHandler(...)} is scoped to THIS RouteBuilder only, so it does not affect
 * {@link ErrorHandlingRoute}. Because a Dead Letter Channel marks the exchange handled once the message is
 * parked, the caller does NOT see an exception. The redelivery numbers are deliberately tiny so the demo
 * and tests stay fast. (The full Dead Letter Channel pattern gets its own dedicated module later.)
 */
@Component
public class DeadLetterChannelRoute extends RouteBuilder {

    @Override
    public void configure() {
        // Replace the default error handler for this builder's routes with a Dead Letter Channel:
        //   run the step; on failure wait 20ms and retry up to 2 more times; if it STILL fails, give up
        //   and move the message to {{ep.dead}} (marked handled, so the caller is not told about the error).
        errorHandler(deadLetterChannel("{{ep.dead}}")
            .maximumRedeliveries(2)
            .redeliveryDelay(20)
            .retryAttemptedLogLevel(LoggingLevel.WARN));

        from("direct:charge")
            .routeId("dlc-demo")
            .log("Charging (with retries) order ${body.orderId}")
            .bean("paymentBean", "process")   // fails for flagged orders -> retried twice, then dead-lettered
            .to("{{ep.charged}}");            // reached only when the charge eventually succeeds
    }
}
