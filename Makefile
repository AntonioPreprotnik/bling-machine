
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
	lein clean && \
	(echo "(start-dev)"; cat <&0) | lein with-profile +dev,+frontend repl

start-repl:
	lein with-profile +dev,+frontend repl

start-services:
	docker-compose up -d

stop-services:
	docker-compose down -v

psql:
	docker-compose exec db psql -U postgres

test:
	lein test

lint:
	lein lint

format-check:
	lein cljfmt check

format-fix:
	lein cljfmt fix

check-migrations:
	lein migrator reset && lein migrator rollback

check-seeds:
	lein seeder reset



npm-deps:
	npm install

ci: format-check lint test check-migrations check-seeds release-app build-docker-image

develop: npm-deps start-services start-app

# --------------------------------------------------
# Production
# --------------------------------------------------

release-frontend:
	lein release-frontend && \
	npm run clean && \
	npm run build

release-app:
	make release-frontend && lein release-app

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
