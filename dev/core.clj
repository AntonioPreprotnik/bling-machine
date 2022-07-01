(ns core
  "This is the entrypoint namespace for development environment. It conitains set of
  functions and callbacks that enable system management and debugging in development environment."
  (:require
   [clojure.core.async :refer  [go]]
   [clojure.repl :as repl]
   [system.core :refer  [restart-system start-system stop-system]]
   [system.nrepl :as nrepl]
   [system.watchers :refer [postcss-watch reset-postcss-watch watch-backend
                            watch-frontend]]))

(defn start-dev
  "Starts development system and runs frontend, backend and css watchers."
  [& _]
  (watch-backend restart-system)
  (watch-frontend)
  (start-system)
  (go (postcss-watch)))

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

  ;;# CSS WATCHER
  ;;# --------------------------------------------------------------------------

  (reset-postcss-watch)

  ;;# EXCEPTION STACKTRACE
  ;;# --------------------------------------------------------------------------

  (repl/pst))
