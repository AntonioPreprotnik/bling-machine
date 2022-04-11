(ns app.core
  (:gen-class)
  (:require [app.config :as config]
            [app.logging :as logging]
            [app.funicular :as funicular]
            [app.penkala :as penkala]
            [app.web :refer [controller-interceptors routes]]
            [framework.db.core :as db]
            [framework.db.seed :as seed]
            [framework.route.core :as routes]
            [framework.webserver.core :as ws]
            [piotr-yuxuan.closeable-map :refer [closeable-map]]))

(def app-config
  {:routes                  routes
   :controller-interceptors controller-interceptors})

(defn ->system
  "Initalization of system configuration and services"
  []
  (-> (config/load-config app-config)
      logging/init!
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
