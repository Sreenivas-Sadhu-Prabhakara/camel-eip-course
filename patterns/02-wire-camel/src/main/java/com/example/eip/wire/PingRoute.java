// SPDX-License-Identifier: Apache-2.0
package com.example.eip.wire;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * A tiny request/reply route that gives the test something deterministic to assert on.
 *
 * <p>It reads a message off {@code direct:ping}, prepends {@code "pong:"} to the body, and sends the
 * result to {@code {{ep.out}}}. The output endpoint is a <b>property placeholder</b>, not a hardcoded
 * URI, so it can resolve to {@code log:out} when the app runs and to {@code mock:out} in tests — the
 * same portability move used throughout this course.
 *
 * <p>{@code direct:} is a synchronous in-memory channel, also built into camel-core: no extra starter.
 */
@Component
public class PingRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("direct:ping")                            // (1) direct: — built-in, in-memory, synchronous
            .routeId("ping")
            .transform(simple("pong:${body}"))         // (2) prepend "pong:" to whatever came in
            .to("{{ep.out}}");                         // (3) placeholder: log:out (run) / mock:out (test)
    }
}
