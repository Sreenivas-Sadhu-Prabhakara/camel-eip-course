<!-- SPDX-License-Identifier: CC-BY-4.0 -->
# EIP Hands-On — Enterprise Integration Patterns with Apache Camel on Spring Boot

[![CI](https://github.com/Sreenivas-Sadhu-Prabhakara/camel-eip-course/actions/workflows/ci.yml/badge.svg)](https://github.com/Sreenivas-Sadhu-Prabhakara/camel-eip-course/actions/workflows/ci.yml)
[![Release](https://img.shields.io/github/v/release/Sreenivas-Sadhu-Prabhakara/camel-eip-course?sort=semver&color=E9A23B)](https://github.com/Sreenivas-Sadhu-Prabhakara/camel-eip-course/releases)
[![Apache Camel 4.18](https://img.shields.io/badge/Apache%20Camel-4.18-E9A23B.svg)](https://camel.apache.org/)
[![Spring Boot 3.5](https://img.shields.io/badge/Spring%20Boot-3.5-6DB33F.svg)](https://spring.io/projects/spring-boot)
[![Live course site](https://img.shields.io/badge/course-live%20site-3FD0C9.svg)](https://sreenivas-sadhu-prabhakara.github.io/enterprise-integration-patterns/)
[![License: Apache-2.0](https://img.shields.io/badge/code-Apache--2.0-blue.svg)](LICENSE)
[![Docs: CC-BY-4.0](https://img.shields.io/badge/docs-CC--BY--4.0-lightgrey.svg)](LICENSE-docs)

Learn **Enterprise Integration Patterns (EIP)** the way you actually use them: **one small, runnable,
tested module per pattern**, built on the **Red Hat build of Apache Camel for Spring Boot** — with a
plain Apache Camel fallback so it works anywhere. Beginner-first: **no prior Spring Boot or Camel
experience required.**

> 📖 **Companion website (courseware):** the guided lessons live in a separate repo →
> [`enterprise-integration-patterns`](https://github.com/Sreenivas-Sadhu-Prabhakara/enterprise-integration-patterns)
> · This repo is the **runnable code**.

## The stack (verified)
| | |
|---|---|
| Product | Red Hat build of Apache Camel for Spring Boot **4.18** |
| Camel BOM | `com.redhat.camel.springboot.platform:camel-spring-boot-bom:4.18.1.redhat-00014` |
| Maven repo | `https://maven.repository.redhat.com/ga` — **public, no login** |
| Spring Boot | 3.5.14 *(4.18 is the last Camel line on Spring Boot 3.x)* |
| Camel | 4.18.1 |
| JDK | **17 or 21** |
| Fallback | `org.apache.camel.springboot:camel-spring-boot-bom:4.18.1` (Maven Central) via `-P upstream` |

## Quickstart
```bash
# Build & test everything (Red Hat build — the default):
./mvnw verify

# No Red Hat access / behind a corporate firewall? Use plain Apache Camel:
./mvnw -P upstream verify

# Run one pattern live:
./mvnw -pl patterns/10-content-based-router spring-boot:run
```
The **Red Hat ⇄ upstream switch** is a single Maven profile; `-P upstream` swaps to Maven Central and
needs no extra repository. CI proves both build green on JDK 17 and 21. Broker tests (module 18) run under
`verify` via Testcontainers — see [TROUBLESHOOTING](TROUBLESHOOTING.md) if Docker isn't available.

## Curriculum — 21 runnable modules ✅
One cumulative **ShopFlow** order-processing story, simple → complex.

| # | Pattern | Broker? |
|---|---|:--:|
| 01 | Spring Boot in 30 minutes (for Camel) | |
| 02 | Wire Camel: the BOM & one-starter-per-component | |
| 03 | Your first route: `from()` / `to()` / `routeId()` | |
| 04 | Message Channel & Endpoint (`direct:` vs `seda:`) | |
| 05 | The Message & the Exchange (bodies, headers, Processors) | |
| 06 | Testing Camel routes | |
| 07 | When a route throws (error handling basics) | |
| 08 | Pipes & Filters | |
| 09 | Message Filter | |
| 10 | Content-Based Router | |
| 11 | Splitter | |
| 12 | Aggregator | |
| 13 | Recipient List | |
| 14 | Wire Tap | |
| 15 | Message Translator (JSON ↔ POJO) | |
| 16 | Content Enricher | |
| 18 | Point-to-Point vs Publish-Subscribe | 🐳 |
| 19 | Dead Letter Channel & Redelivery | |
| 20 | Idempotent Consumer | |
| 21 | Request-Reply vs Event Message | |
| 99 | Capstone: end-to-end ShopFlow pipeline (8 EIPs) | |

Each module is self-contained: a `RouteBuilder`, a JUnit 5 test that *is* the spec, a `README` with a
message-flow diagram, and sample data.

## Repository layout
```
camel-eip-course/
├── pom.xml            # parent aggregator: Camel BOM import + redhat/upstream profiles + failsafe
├── mvnw, .mvn/        # Maven wrapper — no local Maven needed
├── patterns/NN-*/     # one Maven module per EIP (all structurally identical)
├── infra/             # docker-compose: ActiveMQ Artemis + Kafka (make up)
├── .github/workflows/ # CI: verify on JDK 17 & 21 × redhat & upstream
├── LICENSE            # Apache-2.0 (code)
├── LICENSE-docs       # CC-BY-4.0 (prose)
├── CONTRIBUTING.md · TROUBLESHOOTING.md · NOTICE · Makefile
```

## Prerequisites
JDK **17 or 21**, Git, Docker Desktop (only for the messaging module). No Spring Boot, messaging, or Camel
background needed. See [CONTRIBUTING](CONTRIBUTING.md) to add a pattern and [TROUBLESHOOTING](TROUBLESHOOTING.md)
if you get stuck.
