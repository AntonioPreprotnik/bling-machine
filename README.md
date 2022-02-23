# pasta-xiana

FIXME: description

## Usage

### Start dockerized PostgreSQL

```shell
docker-compose up -d
```

### Log into psql console

```shell
docker-compose exec db psql -U postgres
```

### Prepare node-dependencies

```shell
lein shadow npm-deps
```

### Start development

Jack in a repl, execute

```clojure
(user/start-dev-system)
```

It will start up the shadow watch and the backend. It can be used to restart the whole application too.

### Build frontend and run the backend

```shell
lein release && lein run
```

### Try pasta-xiana

```shell
curl http://localhost:3000/
```




lein with-profile +dev,+frontend repl

lein repl :connect 127.0.0.1:54785

(shadow.cljs.devtools.api/repl :app)
