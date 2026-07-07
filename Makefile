# EIP Hands-On — common tasks. Run `make help` for the list.
MVN := ./mvnw -B -ntp
COMPOSE := docker compose -f infra/docker-compose.yml

.PHONY: help build test verify redhat up down logs clean

help: ; @grep -E '^[a-z-]+:.*?; *' $(MAKEFILE_LIST) | sed 's/:.*;/ →/' | sort

build: ; $(MVN) -P upstream -q compile          ## compile all modules (plain Apache Camel)
test: ; $(MVN) -P upstream test                 ## run all unit tests (no Docker needed)
verify: ; $(MVN) -P upstream verify             ## unit + broker integration tests (needs Docker)
redhat: ; $(MVN) test                           ## build & test with the Red Hat build (default profile)
up: ; $(COMPOSE) up -d                          ## start Artemis + Kafka locally
down: ; $(COMPOSE) down -v                      ## stop and remove the infra stack
logs: ; $(COMPOSE) logs -f                      ## tail broker logs
clean: ; $(MVN) clean                           ## remove build output
