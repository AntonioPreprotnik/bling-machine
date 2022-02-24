# PastaXiana

## Development

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
make start-app
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