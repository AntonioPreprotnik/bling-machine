(ns user
  (:require
   [nrepl.server :refer [start-server stop-server]]
   [system]))

(def nrepl-server (atom nil))

(defn start-nrepl []
  (reset! nrepl-server (start-server :port 7888)))

(defn stop-nrepl []
  (stop-server @nrepl-server))

(defn reset-nrepl []
  (stop-nrepl)
  (start-nrepl))

(defn start-dev-with-nrepl [& _]
  (start-nrepl)
  (system/start-dev))

