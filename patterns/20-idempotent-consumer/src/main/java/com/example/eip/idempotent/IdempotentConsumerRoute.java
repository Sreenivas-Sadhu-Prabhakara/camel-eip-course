// SPDX-License-Identifier: Apache-2.0
package com.example.eip.idempotent;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.support.processor.idempotent.MemoryIdempotentRepository;
import org.springframework.stereotype.Component;

/**
 * Idempotent Consumer (EIP): process each message exactly once, silently dropping duplicates.
 *
 * <p>Duplicates are a fact of life in messaging: a broker redelivers after a timeout, a customer taps
 * "Pay" twice, an upstream system replays a batch. This EIP guards the route with an
 * <em>idempotency key</em> — a stable, business-meaningful value that is identical across every copy of
 * the same logical message. Here the key is the {@code orderId} header.
 *
 * <p>{@link org.apache.camel.builder.RouteBuilder#idempotentConsumer idempotentConsumer(key, repo)}
 * remembers each key it has seen in the {@code repository}. The first time a key arrives, the block runs
 * ({@code .to("{{ep.out}}")}); every later message carrying a key already in the repository is skipped.
 * The terminal endpoint is a {@code {{ep.out}}} placeholder so production points it at a real
 * {@code direct:}/{@code jms:} endpoint while tests point it at a {@code mock:} endpoint.
 *
 * <p>{@link MemoryIdempotentRepository#memoryIdempotentRepository()} keeps the seen-keys set in a plain
 * in-memory cache — perfect for a single JVM and for tests, but it forgets on restart and is not shared
 * across instances. In production you would swap in a persistent/shared repository (JDBC, Infinispan,
 * Redis, Hazelcast, Caffeine) so dedup survives restarts and works across a cluster. The DSL is
 * identical; only the repository changes.
 */
@Component
public class IdempotentConsumerRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("direct:orders")                                            // (1) the channel orders arrive on
            .routeId("idempotent")                                       //     always name a route (tracing, metrics, tests)
            .idempotentConsumer(                                          // (2) the Idempotent Consumer itself
                    header("orderId"),                                    // (3) the idempotency key
                    MemoryIdempotentRepository.memoryIdempotentRepository()) // (4) in-memory seen-keys store (swap for JDBC in prod)
                .log("Processing order ${header.orderId} (amount=${body.amount})") // only first-seen keys reach here
                .to("{{ep.out}}")                                        // (5) the exactly-once destination
            .end();                                                      //     close the idempotent block
    }
}
