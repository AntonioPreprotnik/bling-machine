
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

start-app:
	clojure -X:dev:frontend:start-app

start-repl:
	clojure -A:dev:frontend

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

lint:
	clojure -M:lint

format-check:
	clojure -X:dev:cljfmt :cmd :check

format-fix:
	clojure -X:dev:cljfmt :cmd :fix

namespace-checker:
	clojure -X:namespace-checker

check-migrations:
	clojure -X:test:migrator :args '["reset"]' && clojure -X:test:migrator :args '["rollback"]'

check-seeds:
	clojure -X:test:seeder :args '["reset"]'

npm-deps:
	npm install

ci: format-check lint test check-migrations check-seeds release-app build-docker-image

develop: npm-deps start-services start-app

# --------------------------------------------------
# Production
# --------------------------------------------------

release-backend:
	clojure -T:build-uberjar uber

release-frontend:
	npm install && \
	clojure -X:dev:frontend:release-frontend && \
	npm run build

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
