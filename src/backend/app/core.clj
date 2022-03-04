(ns app.core
  (:gen-class)
  (:require
   [app.controllers.keechma :as keechma]
   [app.funicular :as api]
   [app.handlers.funicular :as funicular]
   [app.penkala :as penkala]
   [clojure.edn :as edn]
   [clojure.java.io :as io]
   [com.verybigthings.funicular.transit :as funicular-transit]
   [com.walmartlabs.dyn-edn :refer [env-readers]]
   [framework.db.core :as db]
   [framework.db.seed :as seed]
   [framework.interceptor.core :as interceptors]
   [framework.route.core :as routes]
   [framework.webserver.core :as ws]
   [muuntaja.core :as m]
   [muuntaja.interceptor]
   [piotr-yuxuan.closeable-map :refer [closeable-map]]
   [reitit.ring :as ring]
   [xiana.commons :refer [deep-merge]]
   [xiana.core]))

(def routes
  [["/" {:action #'keechma/handle-index}]
   ["/assets/*" (ring/create-resource-handler {:path "/"})]
   ["/api" {:action #'app.handlers.funicular/handler}]])

(def muuntaja-instance
  (m/create
   (-> m/default-options
       (assoc-in [:formats "application/transit+json" :decoder-opts] funicular-transit/read-handlers)
       (assoc-in [:formats "application/transit+json" :encoder-opts] funicular-transit/write-handlers))))

(def app-config
  {:routes                  routes
   :controller-interceptors [(interceptors/muuntaja (muuntaja.interceptor/format-interceptor muuntaja-instance))
                             interceptors/params]})

(defn- read-config [path]
  (->> (io/resource path)
       (slurp)
       (edn/read-string {:readers (env-readers)})))

(defn- read-default-config []
  (read-config "default.edn"))

(defn- read-env-config []
  (read-config "config.edn"))

(defn load-config []
  (deep-merge (read-default-config)
              (read-env-config)
              app-config))

(defn ->system []
  (-> (load-config)
      db/connect
      penkala/init
      api/init
      routes/reset
      db/migrate!
      seed/seed!
      ws/start
      closeable-map))

(defn -main
  [& _args]
  (->system))
