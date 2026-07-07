<!-- SPDX-License-Identifier: CC-BY-4.0 -->
# Contributing

Thanks for helping improve this course! Contributions of new patterns, clearer explanations, bug fixes,
and translations are all welcome.

## Ground rules
- **Licensing.** Code is Apache-2.0; course prose (module `README.md`, diagrams, site content) is CC-BY-4.0.
  By contributing you agree your contribution is licensed the same way.
- **DCO sign-off.** Sign every commit: `git commit -s` (adds a `Signed-off-by:` line certifying the
  [Developer Certificate of Origin](https://developercertificate.org/)).
- **Keep it green.** `./mvnw -P upstream verify` must pass (Docker needed for the broker module's IT).

## The module contract
Every folder under `patterns/NN-name/` is structurally identical so a learner who has done one module can
navigate any module. A module is complete when it has all of:

1. **`pom.xml`** — minimal: inherits the parent; only the extra `camel-<component>-starter` deps this
   pattern needs (no `<version>` — the BOM manages it).
2. **A `RouteBuilder`** — one `@Component`, every route has a `.routeId()`; assertable endpoints are
   `{{ep.*}}` placeholders (→ `log:` in `src/main/resources/application.yaml`, `mock:` in the test yaml).
3. **A JUnit 5 test** — `@CamelSpringBootTest @SpringBootTest`, `MockEndpoint` assertions; the test *is*
   the spec. Broker-backed tests are named `*IT.java` (run under `verify`, skipped by `test`).
4. **`README.md`** — the six headings: Objective, Scenario, Message flow (mermaid + ASCII), Components used,
   How to run, Test it.
5. **`docs/flow.mmd`** — the mermaid source for the diagram.
6. **Sample data** under `src/test/resources/data/` when the scenario uses payloads.

## Conventions
- Java 17 language level; 4-space indent (`.editorconfig`).
- Never add a `<version>` to a `camel-*` or `spring-boot-*` dependency — the BOM owns versions.
- Both build profiles must work: `redhat` (default) and `-P upstream` (plain Apache Camel).
- Add every new module to the parent `pom.xml` `<modules>` list.
