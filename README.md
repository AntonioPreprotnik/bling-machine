# BlingMachine

## About the project

This is a project whose goal was to have a instant response converter from a list of currencies available  to the hnb.api  (http://api.hnb.hr/). On start, the project pull all the entries for the years 2020-2022, parse and store them in the base. The project offers the ability to change any of the give currencies, the amount of money used or the date of conversion and have the response of the exchange rate and converted amount  be instantly change accordingly. The date cannot be picked, until the two currencies are selected. This is a single page app. Due to hnb.api limitations, adding a new year to the startup process is done by adding the code for the appropriate year in core.clj. 

### Source paths
Three basic source paths (paths key in deps.edn) are defined as:
"src" "config" and "resources"

- "src" path contains backend, frontend and shared application code
- The "config" path contains configuration files for each environment.
- "resources" path contains migration files, other resources and public folder for frontend
  
  In addition to these basic source root folders, the important folders used in some deps aliases as extra-paths are:
  
- The "test" path contains all tests
- The "dev" path consists of files that are used exclusively during development and mainly in interaction with REPL.
- "scripts" contains different auxiliary scripts
  
### Technologies

This project is in line with VBT's commitment to:

- Use the same language everywhere in different projects
- `Clojure` or `ClojureScript` on the backend (with the JVM or Node.JS runtime options)
- ClojureScript on the frontend and mobile with React and React Native
- Clojure for shell scripting with Babashka

The project uses libraries developed in VBT in response to problems and requirements that have arisen in previous agencies projects:
[Penkala](https://github.com/retro/penkala), [Funicular](https://github.com/VeryBigThings/funicular) and [Keechma-next](https://github.com/keechma/keechma-next).

The [Xiana framework](https://github.com/Flexiana/framework) is the chosen library for storing the application state as a better alternative to `Duct`.

The project was initially defined as the `Leiningen` project but we later decided to move to [Clojure Deps](https://clojure.org/guides/deps_and_cli) as a more modern option.

[Shadow-cljs](https://github.com/thheller/shadow-cljs) is chosen as frontend development tool and [tailwind.css](https://tailwindcss.com/) as CSS framework. 

### Before you begin starting the application you need to get up the following:

### System dependencies



1. [Java](https://www.java.com/en/download/manual.jsp) version: `17`
2. [Clojure CLI](https://clojure.org/guides/install_clojure) version: `1.11.1.1129`
3. [Node](https://nodejs.org/en/download/) version: `latest`
4. [Docker](https://docs.docker.com/get-docker/) version: `latest`

Use `asdf` if you prefer it, check .tool-versions file for the exact tool versions.
   
### Tools

1. [Clojure-lsp](https://clojure-lsp.io/installation/) version: `2022.05.31-17.35.50`
2. [Babashka](https://github.com/babashka/babashka#installation) version: `0.8.156`
3. [Clj-kondo](https://github.com/clj-kondo/clj-kondo/blob/master/doc/install.md) version: `2022.05.31`
   
### [Editor](https://clojure.org/guides/editors) with interactive development support
- [Emacs](https://www.gnu.org/software/emacs/)
- [Intellij](https://www.jetbrains.com/idea/)
- [VS Code](https://code.visualstudio.com/)
- [Vim](https://www.vim.org/)
- Other
  
  
### Next install the initial node packages. Run this on first use and after any changes in `Package.json` dependencies:

```shell
 npm install
```

### Then configure your environment

#### Configuration files
Values that are common for all three environments (dev,prod and test) are defined in `config/common.edn` and values that are specific for every environment are defined in corresponding `config.edn` file. Dev and test `config.edn` files have defined default values which is not the case for `prod/config.edn` to prevent possibility of accidentally sending wrong (default) values to production.

#### ENV variables

```shell
 npm install
```   

In order to start the system, you should expose system variables using [direnv](https://direnv.net/) or any other tool of choice in the same process where you start your dev system (every time before starting it). You can find list of needed variables by checking `config.edn` in folders `config/dev,prod,test` or in `.envrc.default` file. Configuration files are using [dyn-env](https://github.com/walmartlabs/dyn-edn) readers to load and cast values from system variables.


### Start external dockerized services

To begin your development session you should run this command first:

```shell
make start-services
```

This command starts PostgreSQL server or any other external service as described in `docker-compose.yml`. In this case it will create and start one container with two databases: one for development and one for testing.

Than you will start development system with this command:

### System management

### Start the development environment

```shell
make start-dev
```
This command is composed of two commands:
- the first one runs function `patch-dev` from Babashka script `frontend-version-patcher` that creates `index.html`  file based on `index.tmpl` for development.
- the second one runs `dev.system.core/start-dev` function with deps aliases `dev` and `frontend`.
- `dev.system.core/start-dev` function loads development system as closable map in `dev-state` atom and starts three watchers:
    - backend watcher
    - frontend watcher
    - postcss watcher
- `dev.system.core/start-dev` function also loads` shadow-cljs nREPL on port 8777` that you can connect to from your editor.
- Webserver will be accessible on `http://localhost:3000`.

#### Watchers

All three watchers are defined in the `dev/system/watchers.clj` file

- The **backend watcher** is using the [Hawk](https://github.com/wkf/hawk) library to watch all changes on files of type .`clj` or `.edn` in folders `src/app/backend`, `src/app/shared` and calls the `restart-system` function when changes are detected. This function calls the `clojure.tools.namespace.repl/refresh` or `refresh-all` and than reloads `dev-state` atom with new state. All calls to `restart-system` function are debounced with `debounce` function in `watchers.clj`
  
  
- The **frontend watcher**

Frontend watcher is provided by `Shadow-cljs` 

### You can install NPM dependencies, start dockerized services and development environment with just one command:

```shell
make develop
```

#### Log in to the psql console

```shell
psql -U postgres -p 5433 -h localhost 
```

#### Restart system

Once system is started you can explicitly reset the system to apply new system configuration after making changes to the codebase. In `core` or `system.core` namespace execute following command:

```clojure
(restart-system true)
```
to reload all namespaces

or 

```clojure
(restart-system false)
```

to reload only modified namespaces. 
This can be used for example after making changes in `dev` folder that is not watched by hawk. 

#### Interactive [Tailwind](https://tailwindcss.com/) development

[postcss](https://postcss.org/) watcher is automatically started with `make start-dev/start-dev-nrepl` commands and its output is redirected to the terminal that was used for starting those commands. 
In the case that you notice that watcher is stopped you can restart it running `reset-postcss-watch` from REPL in `system.watchers` namespace.

## Migration and seeds

You can run migratus commands for the system from the REPL in `helpers.migratus` ns. For example:

```clojure
(migratus/init migration-config)
(migratus/migrate migration-config)
(migratus/reset migration-config)
(migratus/rollback migration-config)
```

## Testing

Application is using [kaocha test runner](https://github.com/lambdaisland/kaocha) for test management. Kaocha configuration for tests is found `/tests.edn` file while kaocha hooks and test fixtures are found in `test/test_core.clj` and `test/test_fixtures.clj` respectively.

You can run test suites with:

```shell
make run-tests
```

In the situation when you want to run tests form just one namespace or folder, or individual tests you can do it using `kaocha REPL`.

To start the REPL in `app.test-repl` namespace that has `kaocha REPL` as required dependency run:

```shell
make start-test
```

**It's mandatory to have all backend code tested, which means at least all calls to endpoints should be tested.**

### Backend tests

In order to start testing system and ensure that system only starts and stops **once** kaocha hooks `test-core/start-test-system` and `test-core/stop-test-system` are triggered on the beginning and the end of the test suite/run. 
To keep clean state of the database between tests `test-fixtures/clean-db` fixture is used for each test.

### Frontend tests

No frontend tests yet 

## CI

There are couple of standards used to ensure code quality between releases:
- formatter checks
- linter checks
- test runners
- migration reversibility checks
- release build checks
  
  All of those are enforced via Github Actions and merges into `develop` or `main` branch won't be available until all workflow checks are passed.
  
  You can do the same checks locally before committing or making a PR by running:
  
```shell
make ci
```

For frequent testing during development there is another command `fast-ci` that doesn't contain long running checks (`check-db-integrity` `release-app`) and `clojure-lsp  diagnostics` is replaced with much faster command `clj-kondo --lint src dev test`

```shell
make fast-ci
```

### Monolith app

Application is built and shipped as monolith artifact which means both frontend and backend are being served from the same server. 

Frontend part of the application is build with command:

```shell
make release-frontend
```

To make a production `.jar` release run:

```shell
make  release-backend
```

`release-frontend` command is using `frontend-version-patcher` script to renames `app.js` and `style.css` by adding timestamp to their names and generates `index.html` file that calls them with modified names.

You can run both release commands with:

```shell
make release-app
```

which will build release artifact under `/target/{jar-name}.jar` path. `{jar-name}` is configured in `build_uberjar.clj` name space.

This `.jar` file can be run with:

```shell
java -jar target/{jar-name}.jar
```

and deployed directly to a running JVM instance or via docker image. 



?? MP

In order to make docker image run:

```shell
make build-docker-image
```

This docker image should be tagged and deployed to Docker repository of choice and used from cloud services of choice.


#### Deploying to Heroku

```shell
make release-app
heroku plugins:install java
heroku deploy:jar target/app.j...
