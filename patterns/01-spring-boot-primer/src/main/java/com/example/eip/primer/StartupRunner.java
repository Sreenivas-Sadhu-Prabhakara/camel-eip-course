// SPDX-License-Identifier: Apache-2.0
package com.example.eip.primer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Runs once, right after the application context is ready.
 *
 * <p>{@link Component @Component} makes this a scanned bean; implementing {@link CommandLineRunner} tells
 * Spring Boot to invoke {@link #run} automatically at the end of startup. That is the natural place to see
 * your beans working — here we just log a greeting.
 *
 * <p>The {@link GreetingService} is supplied through <b>constructor dependency injection</b>: Spring sees
 * the constructor needs a {@code GreetingService}, finds the single bean of that type, and passes it in.
 * The bean is {@code final} and never {@code new}-ed by us — the container owns its lifecycle. The name to
 * greet comes from {@code app.greeting.name} via {@link Value @Value}, so this class holds no hardcoded data.
 */
@Component
public class StartupRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(StartupRunner.class);

    private final GreetingService greetingService;

    /** Injected from {@code app.greeting.name} in application.yaml. */
    @Value("${app.greeting.name}")
    private String name;

    /**
     * Constructor injection — the recommended style: dependencies are explicit, final, and easy to test.
     *
     * @param greetingService the bean Spring wires in for us
     */
    public StartupRunner(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    @Override
    public void run(String... args) {
        log.info(greetingService.greet(name));
    }
}
