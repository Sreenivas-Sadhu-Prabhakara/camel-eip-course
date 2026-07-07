<!-- SPDX-License-Identifier: CC-BY-4.0 -->
# Roadmap

EIP Hands-On is a living course. Here's what's shipped and what's next. Have a request?
[Open an issue](https://github.com/Sreenivas-Sadhu-Prabhakara/camel-eip-course/issues) to propose a
pattern or vote on the next one.

## ✅ Shipped — v1.0
- **18 Enterprise Integration Patterns** as runnable, tested Maven modules (22 lessons total).
- **Foundations** track: Spring Boot on-ramp, first route, channels, the exchange, testing, error handling.
- **Routing** track: Pipes & Filters, Content-Based Router, Filter, Splitter, Aggregator, Recipient List, Wire Tap.
- **Transformation & messaging**: Message Translator, Content Enricher, run-the-brokers, Point-to-Point vs Pub-Sub.
- **Reliability** track: Dead Letter Channel, Idempotent Consumer, Request-Reply vs Event.
- **Capstone** composing eight patterns end-to-end.
- Red Hat build of Apache Camel 4.18 + Spring Boot 3.5, JDK 17/21, plain-Camel `-P upstream` fallback.
- CI on JDK 17 & 21 × Red Hat & upstream; live broker integration test (Testcontainers).
- Companion site: pillar guide, full EIP map, 3 comparison pages, cheat sheet (+ PDF).

## 🔜 Next — planned patterns
Prioritised from the [EIP map](https://sreenivas-sadhu-prabhakara.github.io/enterprise-integration-patterns/eip-map.html) "reference" set:
- **Dynamic Router** · **Routing Slip** · **Resequencer**
- **Scatter-Gather** (multicast + aggregate) as its own module
- **Normalizer** · **Claim Check**
- **Competing Consumers** · **Polling Consumer** · **Transactional Client**
- **Circuit Breaker** (Resilience4j) · **Saga** · **Throttler**

## 🔜 Next — course features
- A **Kafka-first** messaging track mirroring the Artemis lessons.
- More comparison pages (Camel vs Spring Cloud Stream; JMS vs Kafka).
- An **observability** walkthrough (Micrometer + Prometheus, embedded Hawtio route graph).
- Optional video walkthroughs per track.

## How to help
New patterns, clearer explanations, and fixes are welcome — see [CONTRIBUTING.md](CONTRIBUTING.md).
Each new pattern follows the same one-module contract: a route, a JUnit 5 test, a README with a diagram.
