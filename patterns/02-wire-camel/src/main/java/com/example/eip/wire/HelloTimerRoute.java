// SPDX-License-Identifier: Apache-2.0
package com.example.eip.wire;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * The "prove it boots" route: a heartbeat that fires roughly once a second and logs a line.
 *
 * <p>This exists to demonstrate two things at once:
 * <ol>
 *   <li>The {@code CamelContext} really started — if you see the log line, Camel is wired.</li>
 *   <li>{@code timer:} and {@code log:} are <b>built into camel-core</b>: they need no extra
 *       {@code -starter}. (Contrast with {@code jms:}, {@code kafka:}, … which each need their own.)</li>
 * </ol>
 *
 * <p>It is gated by {@code .autoStartup("{{app.demo.enabled}}")}: on by default so
 * {@code spring-boot:run} shows a live heartbeat, but switched OFF in tests
 * (see {@code src/test/resources/application.yaml}) so nothing fires unpredictably.
 */
@Component
public class HelloTimerRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("timer:hello?period=1000")                // (1) timer: — a built-in camel-core component
            .autoStartup("{{app.demo.enabled}}")       //     on for `run`, off for tests (property)
            .routeId("hello-timer")                    //     always name a route (tracing, metrics, tests)
            .setBody(constant("Camel is wired and running ✅"))
            .to("log:hello?showBody=true");            // (2) log: — also built-in; no extra starter
    }
}
