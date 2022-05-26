
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
	bb scripts/clj-kondo.clj

format-check:
	bb scripts/lsp-format.clj dry

format-fix:
	bb scripts/lsp-format.clj

check-namespaces:
	bb scripts/lsp-clean-ns.clj dry

fix-namespaces:
	bb scripts/lsp-clean-ns.clj

# requires babashka/babashka to be installed locally
check-aliases:
	bb scripts/inconsistent_aliases.clj "."

check-migrations:
	clojure -X:test:migrator :args '["check"]'

check-seeds:
	clojure -X:test:seeder :args '["reset"]'

npm-deps:
	npm install

fast-ci: format-check check-namespaces check-aliases lint test

ci: format-check check-namespaces check-aliases lint test check-migrations release-app build-docker-image

develop: npm-deps start-services start-app

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
