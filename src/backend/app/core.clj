(ns app.core
  (:gen-class)
  (:require [app.config :as config]
            [app.funicular :as funicular]
            [app.penkala :as penkala]
            [framework.db.core :as db]
            [framework.db.seed :as seed]
            [framework.route.core :as routes]
            [framework.webserver.core :as ws]
            [piotr-yuxuan.closeable-map :refer [closeable-map]]
            [xiana.core]))

(defn ->system
  "Initalization of system configuration and services"
  []
  (-> (config/load-config)
      db/connect
      db/migrate!
      seed/seed!
      penkala/init
      funicular/init
      routes/reset
      ws/start
      closeable-map))

(defn -main [& _args]
  (->system))
