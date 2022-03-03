# PastaXiana

## Development

### Environment configuration

In order to start the system, you should expose system variables using [direnv](https://direnv.net/) or any other tool of choice. You can find list of needed variables by checking `config/default.edn` and `config/dev/config.edn`. Configuration files are using [dyn-env](https://github.com/walmartlabs/dyn-edn) readers to load and cast values from system variables.


Example of `.envrc` file used by direnv:
```
# PostgreSQL
export PG_HOST=localhost
export PG_DB_NAME=pasta-xiana
export PG_USER=postgres
export PG_PASSWORD=postgres
export PG_PORT=5433

export PG_TEST_HOST=localhost
export PG_DB_TEST_NAME=postgres
export PG_TEST_USER=postgres
export PG_TEST_PASSWORD=postgres
export PG_TEST_PORT=54321

# Web server
export WS_PORT=3000
export WS_TEST_PORT=3000
```

Default values from `config/default.edn` will be overiden by values in `config/dev/config.edn` file. Not every configuration for `dev` environment is the same for `prod` environment so it's the responsibility of the developer to make appropriate adjustment to `config/prod/config.end` file also before shipping application to production.

### Start external dockerized services

```shell
make start-services
```

This command starts PostgreSQL server or any other external service as described in `docker-compose.yml`.


### Start app

```shell
make start-app
```

This command compiles the whole app, starts the nREPL server and automatically runs `(start-dev)` command inside the REPL which start the app system.

You can also start REPL without starting the system automatically by running:

```shell
make start-repl
```

And from there on, take care of the system management by yourself. 

### GOD command

```shell
make develop
```

You can combine both of the above mentioned commands.

### Jack in a nREPL

Use prefered IDE/editor or manually connect to running nREPL server from previous step. If your IDE doesn't automatically detect REPL port, you can find it in `.nrepl-port`.

### System management

#### Start system with watchers

Once in REPL you will be located in `user` namespace. You can execute following command:

```clojure
(user/start-dev)
```

This will start up the backend/frontend watchers and system. Webserver will be accessible on `http://localhost:3000`. Once you start the system with this command, system will take care of restarts on it's own.

#### Start system without watchers

```clojure
(user/start-system)
```

#### Restart system

Once system is started you can explicitly reset the system to apply new system configuration or changes to the codebase. In `user` namespace execute following command.

```clojure
(user/restart-system)
```

#### Stopping system

Once system is started you can reset the system to apply new system configuration or changes to the codebase. In `user` namespace execute following command.

```clojure
(user/stop-system)
```

#### Interactive Tailwind development

In order to enable postcss watcher for tailwind style changes you should run:

```shell
npm develop
```

## Production

Production environment differs from development or test environment mostly by configuration of the system. Configuration files can be found in `config/{environment}` directories. Also, production artifacts and builds are more optimized and minified.

### Backend

To make a production `.jar` release run:

```shell
make release-backend
```

which will build release artifact under `/target/app.jar` path. This `.jar` file can be run with:

```shell
java -jar target/app.jar
```

and deployed directly to a running JVM instance or via docker image. In order to make docker image run:

```shell
make build-docker-image
```

This docker image should be tagged and deployed to Docker repository of choice and used from cloud services of choice.

#### Configuration

In order to run application properly you should expose environment variables on production system. Configuration and needed variables can be found in `config/default.edn` and `config/prod/config.edn` files. Default values from `config/default.edn` will be overiden by values in `config/prod/config.edn` file.

#### Deploying to Heroku

```shell
make release-backend
heroku plugins:install java
heroku deploy:jar target/app.jar --app {app}
```

### Frontend

In order to build production frontend resources run:

```shell
make release-frontend
```

This will build all necessary static files in `resources/public` directory. Those file then can be copied to any static resource host provider.