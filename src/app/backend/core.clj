(ns app.backend.core
  (:gen-class)
  (:require
   [app.backend.config :as config]
   [app.backend.domain.auth :as auth]
   [app.backend.domain.handlers.currencies :as currencies]
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

(defn import-currencies-2020 [config]
  (currencies/import-currencies config {:data {:date-from "2020-01-01"
                                               :date-to "2020-12-31"}})
  config)

(defn import-currencies-2021 [config]
  (currencies/import-currencies config {:data {:date-from "2021-01-01"
                                               :date-to "2021-12-31"}})
  config)

(defn import-currencies-2022 [config]
  (currencies/import-currencies config {:data {:date-from "2022-01-01"
                                               :date-to "2022-12-31"}})
  config)

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
      import-currencies-2020
      import-currencies-2021
      import-currencies-2022
      closeable-map))

(defn -main [& _args]
  (->system))
