(ns backend.core
  (:gen-class)
  (:require
   [backend.config :as config]
   [backend.funicular :as funicular]
   [backend.logging :as logging]
   [backend.penkala :as penkala]
   [backend.web :refer [controller-interceptors routes]]
   [framework.db.core :as db]
   [framework.db.seed :as seed]
   [framework.route.core :as routes]
   [framework.webserver.core :as ws]
   [piotr-yuxuan.closeable-map :refer [closeable-map]]))

(def app-config
  {:routes                  routes
   :controller-interceptors controller-interceptors})

(defn ->system
  "Initialization of system configuration and services"
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
