// SPDX-License-Identifier: Apache-2.0
package com.example.eip.primer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Standard Spring Boot entry point — the exact same shape you'll see in every Camel module.
 *
 * <p>{@link SpringBootApplication @SpringBootApplication} bundles three things:
 * <ul>
 *   <li>{@code @SpringBootConfiguration} — this class can define beans;</li>
 *   <li>{@code @EnableAutoConfiguration} — Spring Boot wires sensible defaults from the classpath;</li>
 *   <li>{@code @ComponentScan} — it scans <em>this package and below</em> for your beans
 *       ({@link GreetingService}, {@link StartupRunner}), so adding a feature is just adding a class.</li>
 * </ul>
 *
 * <p>{@link SpringApplication#run} boots the {@code ApplicationContext} (the bean container), performs the
 * scan, injects dependencies, binds configuration, and then runs any {@code CommandLineRunner} beans.
 * There is no XML and no manual {@code new} — that boilerplate is exactly what Spring Boot removes.
 */
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
