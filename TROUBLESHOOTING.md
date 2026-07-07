<!-- SPDX-License-Identifier: CC-BY-4.0 -->
# Troubleshooting

## `ResolveEndpointFailedException` / "No component found with scheme: xxx"
You used a URI scheme (e.g. `jms:`, `kafka:`, `jackson`) without its starter on the classpath. Add the
matching `org.apache.camel.springboot:camel-<component>-starter` to that module's `pom.xml` (no version).
`direct:`, `seda:`, `log:`, `timer:`, `mock:` and the Simple language are built into `camel-core` and need
no extra starter.

## The build can't reach `maven.repository.redhat.com` (proxy / firewall)
Use the plain Apache Camel build instead — same code, artifacts from Maven Central:
```bash
./mvnw -P upstream verify
```

## "Source option 5 is no longer supported" / wrong Java version
Use **JDK 17 or 21** (the supported line). Check with `java -version`; point `JAVA_HOME` at a 17/21 JDK.
The build compiles to Java 17 bytecode regardless, but the test JVM must be 17+.

## `mvn verify` fails on the broker module (18) — Docker
Module 18's integration tests use Testcontainers and need a running Docker daemon. Start Docker Desktop,
or run only the unit modules with `./mvnw test` (which skips `*IT` tests). To run the app manually instead,
`make up` starts a broker and `./mvnw -pl patterns/18-point-to-point-and-pub-sub spring-boot:run`.

## Port already in use (61616 / 8161 / 9092 / 8080)
Another broker or a previous `make up` is still running. `make down` to stop the infra stack, or free the
port. Ports: Artemis 61616 (JMS) / 8161 (console), Kafka 9092, Kafka UI 8080.

## The app exits immediately when I run it
A Camel app with no long-running consumer can terminate. These modules set
`camel.springboot.main-run-controller: true` to keep the JVM alive; if you copied a route out, set that too.

## A test that uses `seda:`/`wireTap`/`jms:` is flaky
Those hops are asynchronous. Assert with `MockEndpoint` (which waits up to a timeout) or a `NotifyBuilder`,
never a bare `Thread.sleep`.
