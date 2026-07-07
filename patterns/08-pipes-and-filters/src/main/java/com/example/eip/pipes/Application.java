// SPDX-License-Identifier: Apache-2.0
package com.example.eip.pipes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Standard Spring Boot entry point.
 *
 * <p>There is NO Camel wiring here on purpose: the {@code camel-spring-boot-starter} auto-creates a
 * {@code CamelContext} and auto-discovers every {@code RouteBuilder} that is a Spring {@code @Component}.
 * The pipeline itself lives in {@link PipesAndFiltersRoute}; each stage is its own {@code @Component}
 * bean ({@link NormalizeBean}, {@link EnrichBean}, {@link ValidateBean}).
 */
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
