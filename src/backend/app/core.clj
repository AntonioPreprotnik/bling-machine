(ns app.core
  (:gen-class)
  (:require
   [app.controllers.re-frame :as re-frame]
   [framework.config.core :as config]
   [framework.db.core :as db]
   [framework.db.seed :as seed]
   [framework.interceptor.core :as interceptors]
   [framework.rbac.core :as rbac]
   [framework.route.core :as routes]
   [framework.session.core :as session]
   [framework.webserver.core :as ws]
   [piotr-yuxuan.closeable-map :refer [closeable-map]]
   [reitit.ring :as ring]
   [app.penkala :as penkala]
   [app.funicular :as api]
   [app.handlers.funicular :as funicular]
   [xiana.commons :refer [rename-key]]
   [xiana.core]
   [muuntaja.core :as m]
   [muuntaja.interceptor]
   [com.verybigthings.funicular.transit :as funicular-transit]
   [reitit.ring.middleware.muuntaja :as muuntaja]))

(def routes
  [["/" {:action #'re-frame/handle-index}]
   ["/assets/*" (ring/create-resource-handler {:path "/"})]
   ["/api" {:action #'app.handlers.funicular/handler}]])

(defn ->system
  [app-cfg]
  (-> (config/config app-cfg)
      (rename-key :framework.app/auth :auth)
      rbac/init
      session/init-backend
      db/connect
      penkala/init
      api/init
      routes/reset
      db/migrate!
      seed/seed!
      ws/start
      closeable-map))

;; MUUNTAJA INSTANCE ADDED HERE FOR INSTRUCTIONAL PURPOSES

(def muuntaja-instance
  (m/create
   (-> m/default-options
       (assoc-in [:formats "application/transit+json" :decoder-opts] funicular-transit/read-handlers)
       (assoc-in [:formats "application/transit+json" :encoder-opts] funicular-transit/write-handlers))))

(def app-cfg
  {:routes                  routes
   :router-interceptors     []
   :controller-interceptors [(interceptors/muuntaja (muuntaja.interceptor/format-interceptor muuntaja-instance))
                             interceptors/params
                             session/guest-session-interceptor
                             interceptors/view
                             interceptors/side-effect
                             db/db-access
                             rbac/interceptor]})

(defn -main
  [& _args]
  (->system app-cfg))
