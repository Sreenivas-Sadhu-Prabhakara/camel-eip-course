// SPDX-License-Identifier: Apache-2.0
package com.example.eip.primer;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

/**
 * The test IS the specification for this primer.
 *
 * <p>{@link SpringBootTest @SpringBootTest} boots the real {@code ApplicationContext} — the same container
 * {@code main()} would build — using the test profile's {@code application.yaml}. That proves component
 * scanning, bean creation, constructor injection and {@code @Value} binding all wire up correctly.
 */
@SpringBootTest
class GreetingServiceTest {

    @Autowired
    ApplicationContext context;

    @Autowired
    GreetingService greetingService;

    /** Read the same property the service binds, so the assertion follows the config instead of hardcoding it. */
    @Value("${app.greeting.prefix}")
    String configuredPrefix;

    @Test
    void applicationContextLoads() {
        // If the beans could not be created or wired, @SpringBootTest would fail before we get here.
        assertThat(context).isNotNull();
        assertThat(greetingService).isNotNull();
    }

    @Test
    void greetIncludesConfiguredPrefixAndName() {
        String result = greetingService.greet("Sri");

        assertThat(result)
            .contains(configuredPrefix)   // the @Value-injected prefix from application.yaml
            .contains("Sri");             // the name we passed in
    }
}
