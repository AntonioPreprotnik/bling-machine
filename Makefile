
.PHONY: start-app start-repl start-services stop-services psql develop

# --------------------------------------------------
# Development
# --------------------------------------------------

start-app:
	(echo "(start-dev)"; cat <&0)  | lein with-profile +dev,+frontend repl

start-repl:
	lein with-profile +dev,+frontend repl

start-services:
	docker-compose up -d

stop-services:
	docker-compose down -v

psql:
	docker-compose exec db psql -U postgres

develop: start-services start-app

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