
.PHONY: \
		start-app \
		start-repl \
		start-services \
		stop-services \
		psql \
		test \
		lint \
		format-check \
		format-fix
		ci \
		develop \

		release-frontend \
		release-app \
		build-docker-image \
		run-docker-image

DEFAULT_GOAL: help

# --------------------------------------------------
# Development
# --------------------------------------------------
patch-dev:
	bb -m frontend-version-patcher/patch-dev

start-app:
	bb -m frontend-version-patcher/patch-dev && \
	clojure -M:dev:frontend -m nrepl.cmdline

start-services:
	docker-compose up -d

stop-services:
	docker-compose down -v

psql:
	docker-compose exec db psql -U postgres

test:
	clojure -X:test

test-repl:
	clojure -A:test

check-warnings:
	clojure-lsp  diagnostics

check-lint:
	clj-kondo --lint src dev test

check-formatting:
	clojure-lsp format --dry

fix-formatting:
	clojure-lsp format

check-namespaces:
	clojure-lsp clean-ns --dry

fix-namespaces:
	clojure-lsp clean-ns

check-aliases:
	bb scripts/inconsistent_aliases.clj "."

check-db-integrity:
	clojure -X:test:db-integrity

npm-deps:
	npm install

fast-ci: check-formatting check-namespaces check-aliases check-lint test

ci: check-formatting check-namespaces check-warnings test check-db-integrity release-app

develop: npm-deps start-services start-app

# --------------------------------------------------
# Production
# --------------------------------------------------

release-backend:
	clojure -T:build-uberjar uber

release-frontend:
	npm install && \
	bb -m frontend-version-patcher/clear-resources && \
	clojure -X:build:frontend:release-frontend && \
	npm run build && \
	bb -m frontend-version-patcher/patch-prod

release-app: release-frontend release-backend

build-docker-image:
	docker build -t pasta-xiana:latest .

run-docker-image:
	docker run pasta-xiana:latest

# --------------------------------------------------
# Help menu
# --------------------------------------------------

help:
	@echo "Please use \`make <target>' where <target> is one of\n\n"
	@awk '/^[a-zA-Z\-\_\/0-9]+:/ { \
		helpMessage = match(lastLine, /^## (.*)/); \
		if (helpMessage) { \
			helpCommand = substr($$1, 0, index($$1, ":")); \
			helpMessage = substr(lastLine, RSTART + 3, RLENGTH); \
			printf "%-30s %s\n", helpCommand, helpMessage; \
		} \
	} \
	{ lastLine = $$0 }' $(MAKEFILE_LIST)
