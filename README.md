# PastaXiana

## Development

### System dependencies

1. [Java](https://www.java.com/en/download/manual.jsp)
2. [Clojure](https://clojure.org/releases/downloads)
3. [Lein](https://leiningen.org/)
4. [Docker](https://docs.docker.com/get-docker/)
5. [Node](https://nodejs.org/en/download/)

### Environment configuration

In order to start the system, you should expose system variables using [direnv](https://direnv.net/) or any other tool of choice. You can find list of needed variables by checking `config/default.edn` and `config/dev/config.edn`. Configuration files are using [dyn-env](https://github.com/walmartlabs/dyn-edn) readers to load and cast values from system variables.

Default values from `config/default.edn` will be overiden by values in `config/dev/config.edn` file. Not every configuration for `dev` environment is the same for `prod` environment so it's the responsibility of the developer to make appropriate adjustment to `config/prod/config.end` file also before shipping application to production.

### Application file hiararchy

```
|- config/
  |- default.edn
  |- prod/
    |- config.edn (Production configuration file)
  |- dev/
    |- config.edn (Development configuration file)
  |- test/
    |- config.edn (Test configuration file)
|- dev/ (Namespaces used for REPL development and debuging)
|- resources/ (Containts public assets and migration/seed files)
|- src/
  |- backend/
    |- app/
      |- domain/ (Contains logic related to DB relations and funicular handlers)
      |- web/ (Everything related to WEB/API layer)
      |- core.clj (System entrypoint)
  |- frontend/
    |- app/
      |- controllers/ (Frontend routers)
      |- ui/ (UI components)
      |- core.clj
  |- shared/ (Contains files which are used by both backend and frontend)
|- test
  |- backend/ (Contains tests for backend which mostely corelate to backend files and file paths)
  |- frontend/ (Contains frontend integration browser driver testing using webdriver)
  |- test-core.clj (Defines testing system entrypoint and kaocha hooks)
  |- test-fixtures.clj (Defines fixtures for tests)
```

### Start external dockerized services

```shell
make start-services
```

This command starts PostgreSQL server or any other external service as described in `docker-compose.yml`. In this case it will create and start one containter with two databases: one for development and one for testing.


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

### Main command

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

#### Interactive [Tailwind](https://tailwindcss.com/) development

In order to enable [postcss](https://postcss.org/) watcher for tailwind style changes you should run:

```shell
npm develop
```

## Testing

Application is using [kaocha test runner](https://github.com/lambdaisland/kaocha) for test management. Kaocha configuration for tests is found `/tests.edn` file while kaocha hooks and test fixtures are found in `test/test_core.clj` and `test/test_fixtures.clj` respectivly.

You can run test suites with:
```shell
make test
```

**It's mandatory to have all backend code tested, which means at least all calls to endpoints should be tested.**

### Backend tests

In order to start testing system and ensure that system only starts and stops **once** kaocha hooks `test-core/start-test-system` and `test-core/stop-test-system` are triggered on the begining and the end of the test suite/run. While for keeping clean state of the database between tests `test-fixtures/clean-db` fixture is used for each test.

### Frontend tests

## CI

There are couple of standards used to ensure code quality between releases:
  - test runners
  - linter checks
  - formater checks
  - migration reversibility checks
  - release build checks

All of those are enforced via Github Actions and merges into `develop` or `main` branch won't be available until all workflow checks are passed.

You can do the same checks locally before commiting or making a PR by running:
```shell
make ci
```

## Production

Production environment differs from development or test environment mostly by configuration of the system. Configuration files can be found in `config/{environment}` directories. Also, production artifacts and builds are more optimized and minified.

### Monolith app

Application is built and shipped as monolith artifact which means both frontend and backend are being served from the same server. To make a production `.jar` release run:

```shell
make release-app
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
make release-app
heroku plugins:install java
heroku deploy:jar target/app.jar --app {app}
```

### Frontend

In order to build production frontend resources separately in case of spliting backend and frontend run:

```shell
make release-frontend
```

This will build all necessary static files in `resources/public` directory. Those file then can be copied to any static resource host provider.
