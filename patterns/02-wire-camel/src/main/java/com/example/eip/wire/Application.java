// SPDX-License-Identifier: Apache-2.0
package com.example.eip.wire;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Standard Spring Boot entry point — and the whole point of this module.
 *
 * <p>There is NO Camel wiring here on purpose. Because {@code camel-spring-boot-starter} is on the
 * classpath, Spring Boot auto-configures it: it creates a single {@code CamelContext} bean and
 * auto-discovers every {@code RouteBuilder} that is a Spring {@code @Component}. So "adding Camel"
 * is just adding one starter dependency, and "adding a route" is just adding one class — see
 * {@link HelloTimerRoute} and {@link PingRoute}.
 */
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
