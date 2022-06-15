(ns core
  (:require
   [clojure.core.async :refer  [go]]
   [system.core :refer  [restart-system start-system stop-system]]
   [system.n-repl :as n-repl]
   [system.watchers :refer [postcss-watch watch-backend watch-frontend]]))

(defn start-dev
  "Starts development system and runs watcher for auto-restart."
  [& _]
  (watch-backend restart-system)
  (watch-frontend)
  (start-system)
  (go (postcss-watch)))

(defn start-dev-with-nrepl [& _]
  (n-repl/start-nrepl)
  (start-dev))

(comment
  (start-dev)
  (start-system)
  (stop-system)
  (restart-system false))
