
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

start-dev:
	bb -m frontend-version-patcher/patch-dev && \
	clojure -X:dev:frontend 'core/start-dev'

start-dev-nrepl:
	bb -m frontend-version-patcher/patch-dev && \
	clojure -X:dev:frontend 'core/start-dev-with-nrepl'

start-services:
	chmod +x scripts/pg_init_scripts/multiple_databases.sh && \
	docker-compose up --remove-orphans -d

stop-services:
	docker-compose down

clean-services:
	docker-compose down -v

psql:
	docker-compose exec db psql -U postgres

run-tests:
	clojure -X:test

start-test:
	 clojure -M:test -r

check-warnings:
	clojure-lsp diagnostics

check-lint:
	clj-kondo --lint src dev test

check-formatting:
	clojure-lsp format --dry && \
	clojure-lsp clean-ns --dry

fix-formatting:
	clojure-lsp format && \
	clojure-lsp clean-ns

check-aliases:
	bb scripts/inconsistent_aliases.clj "."

check-db-integrity:
	clojure -X:test:db-integrity

npm-deps:
	npm install

fast-ci: check-formatting check-aliases check-lint run-tests

ci: check-formatting check-warnings run-tests check-db-integrity release-app

develop: npm-deps start-services start-dev

# --------------------------------------------------
# Production
# --------------------------------------------------

release-backend:
	clojure -T:build-uberjar uber

release-frontend:
	npm install && \
	bb -m frontend-version-patcher/clear-resources && \
	clojure -X:dev:frontend:release-frontend && \
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
