(ns user
  (:require
   [clojure.core.async :refer  [go]]
   [system.n-repl :as n-repl]
   [system.core :refer  [start-system stop-system restart-system]]
   [system.watchers :refer [watch-backend watch-frontend postcss-watch]]))

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
  (restart-system))
