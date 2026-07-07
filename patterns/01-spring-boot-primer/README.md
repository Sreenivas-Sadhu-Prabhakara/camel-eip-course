<!-- SPDX-License-Identifier: CC-BY-4.0 -->
# 01 · Spring Boot in 30 Minutes (for Camel)

## Objective
Get comfortable with the **Spring Boot foundations** that every later module relies on — **before** any
Camel appears. By the end you can read a `@SpringBootApplication`, know what a *bean* is, follow *dependency
injection*, and bind settings from `application.yaml` with `@Value`. There is **no Camel** in this module on
purpose: once these ideas click, the Camel modules are just "the same Spring Boot app, plus routes."

## Scenario
A one-screen app that greets someone at startup:

| Piece | Role |
|---|---|
| `Application` | `@SpringBootApplication` entry point — boots the context and scans this package |
| `GreetingService` (`@Service`) | builds `"<prefix>, <name>!"`; the **prefix** comes from config via `@Value` |
| `StartupRunner` (`@Component`, `CommandLineRunner`) | runs once at startup, gets `GreetingService` by **constructor injection**, logs `greet(name)` for a **name** from config |
| `application.yaml` | holds `app.greeting.prefix` and `app.greeting.name` — change these, change the output, no recompile |

**How this maps to Camel next:** the next module (`03-content-based-router`) is the *same* Spring Boot app.
`@SpringBootApplication` + component scanning is unchanged; a Camel `RouteBuilder` is just another
`@Component` bean; and the `@Value("${app.greeting.prefix}")` config trick becomes Camel's
`{{ep.domestic}}` endpoint placeholders. You already know the scaffolding — Camel only adds routes.

## Message flow
```mermaid
flowchart LR
    RUN([SpringApplication.run]) --> SCAN{@SpringBootApplication<br/>component scan}
    SCAN --> GS[GreetingService @Service]
    SCAN --> SR[StartupRunner @Component]
    SR -->|constructor DI| GS
    CFG[(application.yaml<br/>app.greeting.*)] -->|@Value prefix| GS
    CFG -->|@Value name| SR
    SR -->|greet name| LOG[[logs the greeting]]
```
`SpringApplication.run --scan--> {GreetingService, StartupRunner}; StartupRunner --DI--> GreetingService; app.greeting.* --@Value--> both; StartupRunner logs greet(name)`

## Components used
| Dependency | Why |
|---|---|
| `spring-boot-starter` | the Spring Boot core: `ApplicationContext`, component scanning, `@Service`/`@Component` beans, constructor DI, `@Value`/`application.yaml` config binding, and auto-configuration (it replaces the XML/`new` boilerplate you'd write by hand) |
| `spring-boot-starter-test` *(test scope)* | JUnit 5 + Spring Test — spins up the real context for `@SpringBootTest` |

No Camel, no broker, no network — everything runs in-memory.

## How to run
```bash
# From the repo root. Red Hat build (default):
./mvnw -pl patterns/01-spring-boot-primer spring-boot:run
# Behind a firewall / no Red Hat access — plain Apache Camel profile (same result; no Camel deps here):
./mvnw -P upstream -pl patterns/01-spring-boot-primer spring-boot:run
```
On startup you'll see a single line like `Hello, World!` from `StartupRunner`. Try overriding config without
editing files:
```bash
./mvnw -pl patterns/01-spring-boot-primer spring-boot:run \
  -Dspring-boot.run.arguments="--app.greeting.prefix=Namaste --app.greeting.name=Sri"
# -> Namaste, Sri!
```

## Test it
```bash
./mvnw -pl patterns/01-spring-boot-primer test
```
`GreetingServiceTest` boots the real `ApplicationContext` (proving the beans wire up), then asserts that
`greet("Sri")` contains both the **configured prefix** (read from `application.yaml` via `@Value`, so the
test follows config rather than hardcoding it) and the **name** `Sri`. Read the test as the spec.
