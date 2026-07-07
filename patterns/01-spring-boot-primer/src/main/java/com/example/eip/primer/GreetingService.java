// SPDX-License-Identifier: Apache-2.0
package com.example.eip.primer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * A trivial business bean — the kind of "do one thing" class Spring is built around.
 *
 * <p>{@link Service @Service} marks this class as a Spring-managed bean, so component scanning picks it up
 * and the container creates exactly one instance you can inject anywhere (see {@link StartupRunner}).
 *
 * <p>The greeting prefix is NOT hardcoded: {@link Value @Value} binds the {@code app.greeting.prefix}
 * property from {@code application.yaml} (or an env var / command-line override) straight into the field.
 * Change the config, change the behaviour — no recompile. This is the same externalised-configuration idea
 * the Camel modules use for their endpoint placeholders.
 */
@Service
public class GreetingService {

    /** Injected from {@code app.greeting.prefix} in application.yaml via a normal Spring placeholder. */
    @Value("${app.greeting.prefix}")
    private String prefix;

    /**
     * @param name who to greet
     * @return the configured prefix followed by the name, e.g. {@code "Hello, Sri!"}
     */
    public String greet(String name) {
        return prefix + ", " + name + "!";
    }
}
