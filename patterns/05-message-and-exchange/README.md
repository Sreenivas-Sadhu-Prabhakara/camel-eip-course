<!-- SPDX-License-Identifier: CC-BY-4.0 -->
# 05 · The Message and the Exchange: Bodies, Headers, Processors

## Objective
Get comfortable with the two objects every Camel route is built around, and learn to **set and read
headers** — the metadata later EIPs route on:

- **Exchange** — the *envelope* Camel carries down a route (an id, exchange properties, and the current message).
- **Message** — `exchange.getMessage()`: a **body** (the payload) plus **headers** (control metadata that
  travels alongside the body but is *not* part of it).

You'll do the same job **two ways** — imperatively with a `Processor`, and declaratively with the DSL —
so the trade-off is concrete.

## Scenario
An order arrives on `direct:orders` as a plain `java.util.Map` with `orderId`, `country`, and `amount`.
Downstream patterns (routers, filters, aggregators) shouldn't have to re-parse the body every time they
need to make a decision, so we **lift the routing-relevant fields up into headers** once, at the edge:

| Field | Copied into header | How |
|---|---|---|
| `orderId` | `orderId` | **imperative** — `OrderHeadersProcessor` reads `exchange.getMessage()` |
| `country` | `country` | **imperative** — same Processor |
| `amount`  | `amount`  | **declarative** — `setHeader("amount", simple("${body[amount]}"))` |

The terminal endpoint is a **property placeholder** (`{{ep.out}}`). In production it'd be a `direct:`/`jms:`
endpoint; in tests it resolves to a `mock:` endpoint so we can assert exactly which headers were set.

## Message flow
```mermaid
flowchart LR
    IN([direct:orders]) --> P[Processor<br/>exchange.getMessage → copy orderId, country to headers]
    P --> S[setHeader amount<br/>simple ${body[amount]}]
    S --> OUT[ep.out]
```
`direct:orders --Processor sets orderId,country headers--> setHeader amount (${body[amount]}) --> ep.out`

## Components used
| Dependency | Why |
|---|---|
| `camel-spring-boot-starter` | boots the CamelContext + auto-discovers routes; provides `direct:`, `log:`, `mock:`, `timer:` and the Simple language (all in `camel-core`) |

No broker needed — this pattern runs entirely in-memory.

## How to run
```bash
# From the repo root. Red Hat build (default):
./mvnw -pl patterns/05-message-and-exchange spring-boot:run
# Behind a firewall / no Red Hat access — plain Apache Camel:
./mvnw -P upstream -pl patterns/05-message-and-exchange spring-boot:run
```
A demo feeder injects a rotating sample `Map` order every 3s. Because `ep.out` is
`log:out?showBody=true&showHeaders=true`, you'll see each message land with its **headers** populated —
`orderId`, `country`, and `amount` — proving the metadata now rides with the message.

## Test it
```bash
./mvnw -pl patterns/05-message-and-exchange test
```
Two tests send a `Map` order to `direct:orders` and assert that the message on `mock:out` carries the
expected `orderId` and `country` **headers** (set by the Processor) plus `amount` (set declaratively) — and
that those values are derived from each message, not hardcoded. Read the test as the spec.
