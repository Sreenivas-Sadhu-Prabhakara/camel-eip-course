<!-- SPDX-License-Identifier: CC-BY-4.0 -->
# infra — local brokers

Only the messaging module (`18-point-to-point-and-pub-sub`) needs a broker when you run it by hand. Its
tests start their own broker via Testcontainers, so you don't need this for `mvn verify`.

```bash
make up      # start Artemis + Kafka + Kafka UI
make logs    # tail their logs
make down    # stop and clean up
```

| Service | Port | URL / use |
|---|---|---|
| ActiveMQ Artemis (JMS) | 61616 | `spring.artemis.broker-url=tcp://localhost:61616` |
| Artemis web console | 8161 | http://localhost:8161 (camel / camel) |
| Kafka | 9092 | `camel.component.kafka.brokers=localhost:9092` |
| Kafka UI | 8080 | http://localhost:8080 |

Credentials are dev-only. This stack is not for production.
