(ns core
  "This is the entrypoint namespace for development environment. It conitains set of
  functions and callbacks that enable system management and debugging in development environment."
  (:require
   [clojure.core.async :refer [go]]
   [clojure.repl :as repl]
   [shadow.cljs.devtools.server :as shadow.server]
   [system.core :refer  [restart-system start-system stop-system]]
   [system.nrepl :as nrepl]
   [system.watchers :as watchers]))

(defn start-dev
  "Starts development system and runs frontend, backend and css watchers."
  [& _]
  (start-system)
  (watchers/watch-backend restart-system)
  (shadow.server/start!)
  (watchers/watch-frontend)
  (go (watchers/postcss-watch)))

(defn start-dev-with-nrepl
  "Creates nREPL session and starts development system."
  [& _]
  (nrepl/start-nrepl)
  (start-dev))

(comment

  ;;# MANUAL SYSTEM MANAGEMENT
  ;;# --------------------------------------------------------------------------

  (start-dev)
  (start-system)
  (stop-system)
  (restart-system)

  ;;# WATCHERS
  ;;# --------------------------------------------------------------------------

  (watchers/watch-backend restart-system)
  (watchers/stop-backend-watcher)
  (watchers/watch-frontend)
  (watchers/reset-postcss-watch)

  ;;# EXCEPTION STACKTRACE
  ;;# --------------------------------------------------------------------------

  (repl/pst))
