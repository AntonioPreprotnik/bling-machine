# PastaXiana

## Development

### System dependencies

1. [Java](https://www.java.com/en/download/manual.jsp)
2. [Clojure](https://clojure.org/releases/downloads)
3. [Lein](https://leiningen.org/)
4. [Docker](https://docs.docker.com/get-docker/)
5. [Node](https://nodejs.org/en/download/)

### To install initial node packages run in the project directory:

```shell
 npm instal
```

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
      |- config.clj (with load-config as main function)
      |- db.clj (migration ans seed functionality)
      |- funicular.clj (funicular initization)
      |- penkala.clj (penkala initization)
      |- readers.clj (custom readers)
      |- web.clj (routes and interceptors init)
  |- frontend/
    |- app/
      |- controllers/ (Frontend routers)
      |- ui/ (UI components)
      |- core.cljs
      |- app.cljs
      
  |- shared/ (Contains cljc files which are used by both backend and frontend)
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

This command compiles the whole app and runs `(start-dev)` from `dev/user` namespace.

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
(start-dev)
```

This will start up the backend/frontend watchers and system. Webserver will be accessible on `http://localhost:3000`. Once you start the system with this command, system will take care of restarts on it's own.

#### Start system without watchers

```clojure
(start-system)
```

#### Restart system

Once system is started you can explicitly reset the system to apply new system configuration or changes to the codebase. In `user` namespace execute following command.

```clojure
(restart-system)
```

#### Stopping system

Once system is started you can reset the system to apply new system configuration or changes to the codebase. In `user` namespace execute following command.

```clojure
(stop-system)
```

#### Interactive [Tailwind](https://tailwindcss.com/) development

In order to enable [postcss](https://postcss.org/) watcher for tailwind style changes you should run:

```shell
yarn develop
```

## Testing

Application is using [kaocha test runner](https://github.com/lambdaisland/kaocha) for test management. Kaocha configuration for tests is found `/tests.edn` file while kaocha hooks and test fixtures are found in `test/test_core.clj` and `test/test_fixtures.clj` respectivly.

You can run test suites with:
```shell
make test
```

In the situation when you want to run tests form just one namespace or folder, or individual tests you can do it using `caocha REPL`.

You can start the REPL in `test/user` namespace that has `caocha REPL` as required dependency with:
```shell
make test-repl
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

which will build release artifact under `/target/{jar-name}.jar` path. `{jar-name}` is configured in `build_uberjar.clj` name space.

This `.jar` file can be run with:

```shell
java -jar target/{jar-name}.jar
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


## List of used libraries

### VBT libraries

### [clojure-commons](https://github.com/VeryBigThings/clojure-commons)

- Small utilities shared across VeryBigThings projects.


### [funicular](https://github.com/VeryBigThings/funicular)

- Funicular allows you to send Clojure data structures over the wire with a minimal ceremony with schema-based data validation as it travels from backend to frontend and separation of commands (mutations) and queries.

### [pgerrors](https://github.com/VeryBigThings/pgerrors)

- Small utility library to extract data from PostgreSQL errors.

### [penkala](https://github.com/retro/penkala)

- Penkala is a composable query builder for PostgreSQL written in Clojure.

### Keechma-next libraries

### [keechma-next](https://github.com/keechma/keechma-next)

- Keechma/next is the second iteration of the Keechma framework. In its scope, it's similar to Integrant - a data driven, state management framework for single page apps.

### [keechma-next-toolbox](https://github.com/keechma/keechma-next-toolbox)

- A set of libraries that make working with Keechma easier.

### [keechma-malli-forms](https://github.com/keechma/keechma-malli-forms)

- Base implementation of form record with live validation based on Malli

### Clojure libraries

### [clojure](https://github.com/clojure/clojure)

- The Clojure programming language

### [clojurescript](https://github.com/clojure/clojurescript)

- ClojureScript is a compiler for Clojure that targets JavaScript. It is designed to emit JavaScript code which is compatible with the advanced compilation mode of the Google Closure optimizing compiler.

### [core.async](https://github.com/clojure/core.async)

- Facilities for async programming and communication in Clojure

### [core.match](https://github.com/clojure/core.match)

- An optimized pattern matching library for Clojure. It supports Clojure 1.5.1 and later as well as ClojureScript.

### [spec.alpha](https://github.com/clojure/spec.alpha)

- spec is a Clojure library to describe the structure of data and functions. Specs can be used to validate data, conform (destructure) data, explain invalid data, generate examples that conform to the specs, and automatically use generative testing to test functions

### [tools.namespace](https://github.com/clojure/tools.namespace)

- Tools for managing namespaces in Clojure. Parse ns declarations from source files, extract their dependencies, build a graph of namespace dependencies within a project, update that graph as files change, and reload files in the correct order.

### Flexiana

### [Xiana framework](https://github.com/Flexiana/framework)

- Xiana is a lightweight web-application framework written in Clojure, for Clojure.

### DB  libraries

### [migratus](https://github.com/yogthos/migratus)

- A general migration framework, with implementations for migrations as SQL scripts or general Clojure code.

### [next-jdbc](https://github.com/seancorfield/next-jdbc)

- A modern low-level Clojure wrapper for JDBC-based access to databases

### [hugsql](https://github.com/layerware/hugsql)

A Clojure library for embracing SQL.

### Metosin libraries

### [malli](https://github.com/metosin/malli)

- Data-Driven Schemas for Clojure/Script.

### [jsonista](https://github.com/metosin/jsonista)

- Clojure library for fast JSON encoding and decoding.

### [reitit](https://github.com/metosin/reitit)

- A fast data-driven router for Clojure/Script

### [muuntaja](https://github.com/metosin/muuntaja)

- Clojure library for fast http api format negotiation, encoding and decoding

### Utilities

### [medley](https://github.com/weavejester/medley)

- A lightweight library of useful Clojure functions

### [dyn-edn](https://github.com/walmartlabs/dyn-edn)

- Dynamic properties in EDN content

### [js-interop](https://github.com/applied-science/js-interop)

- A JavaScript-interop library for ClojureScript.

### [hodgepodge](https://github.com/funcool/hodgepodge)

- A idiomatic ClojureScript interface to local and session storage

### [hawk](https://github.com/wkf/hawk)

- A Clojure library designed to watch files and directories.

### [transit-cljs](https://github.com/cognitect/transit-cljs)

- Transit is a data format and a set of libraries for conveying values between applications written in different languages.

### [closeable-map](https://github.com/piotr-yuxuan/closeable-map)

- Application state management made simple: a Clojure map that implements java.io.Closeable.

### Frontend libraries

### [fetch](https://github.com/lambdaisland/fetch)

- ClojureScript wrapper for the JavaScript fetch API

### [helix](https://github.com/lilactown/helix)

- A simple, easy to use library for React development in ClojureScript.

### [shadow-cljs](https://github.com/thheller/shadow-cljs)

- shadow-cljs provides everything you need to compile your ClojureScript code with a focus on simplicity and ease of use.

### Node.js libraries

### [js-joda](https://www.npmjs.com/package/@js-joda/core)

- Immutable date and time library for JavaScript

### [react](https://www.npmjs.com/package/react)

- React is a JavaScript library for creating user interfaces.

### [tailwindcss](https://libraries.io/npm/tailwindcss)

- A utility-first CSS framework for rapidly building custom user interfaces.

### [postcss](https://www.npmjs.com/package/postcss)

- PostCSS is a tool for transforming styles with JS plugins.

### Debugging/dev libraries

### [cljs-devtools](https://github.com/binaryage/cljs-devtools)

- A collection of Chrome DevTools enhancements for ClojureScript developers

### [nrepl](https://github.com/nrepl/nrepl)

- A Clojure network REPL that provides a server and client, along with some common APIs of use to IDEs and other tools that may need to evaluate Clojure code in remote environments.

### [reveal](https://github.com/vlaaad/reveal)

- Read Eval Visualize Loop for Clojure

### Testing libraries

### [kaocha](https://github.com/lambdaisland/kaocha)

- Full featured next gen Clojure test runner

### [state-flow](https://github.com/nubank/state-flow)

- Integration testing framework using a state monad in the backend for building and composing flows

### [test.check](https://github.com/clojure/test.check)

- test.check is a Clojure property-based testing tool inspired by QuickCheck

### [clj-kondo](https://github.com/clj-kondo/clj-kondo)

- Clj-kondo performs static analysis on Clojure, ClojureScript and EDN, without the need of a running REPL. It informs you about potential errors while you are typing.



