(ns app.backend.core
  (:gen-class)
  (:require
   [app.backend.config :as config]
   [app.backend.domain.auth :as auth]
   [app.backend.funicular :as funicular]
   [app.backend.logging :as logging]
   [app.backend.penkala :as penkala]
   [app.backend.web :refer [controller-interceptors routes]]
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
      auth/init
      penkala/init
      funicular/init
      routes/reset
      ws/start
      closeable-map))

(defn -main [& _args]
  (->system))
