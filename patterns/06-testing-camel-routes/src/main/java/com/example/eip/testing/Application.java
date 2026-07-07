// SPDX-License-Identifier: Apache-2.0
package com.example.eip.testing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Standard Spring Boot entry point.
 *
 * <p>There is NO Camel wiring here on purpose: the {@code camel-spring-boot-starter} auto-creates a
 * {@code CamelContext} and auto-discovers every {@code RouteBuilder} that is a Spring {@code @Component}.
 * The routes under test live in {@link SampleRoute}; the real lesson is the test classes.
 */
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
