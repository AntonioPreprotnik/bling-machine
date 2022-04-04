(ns app.web
  (:require [app.web.controllers.funicular :as funicular]
            [app.web.controllers.keechma :as keechma]
            [app.web.controllers.health :as health]
            [com.verybigthings.funicular.transit :as funicular-transit]
            [framework.interceptor.core :as interceptors]
            [muuntaja.core :as muntaja]
            [muuntaja.interceptor :as interceptor]
            [reitit.ring :as ring]))

(def muuntaja-instance
  (muntaja/create
   (-> muntaja/default-options
       (assoc-in [:formats "application/transit+json" :decoder-opts] funicular-transit/read-handlers)
       (assoc-in [:formats "application/transit+json" :encoder-opts] funicular-transit/write-handlers))))

(def controller-interceptors
  [(interceptors/muuntaja (interceptor/format-interceptor muuntaja-instance))
   interceptors/params])

(def routes
  [["/" {:action #'keechma/handle-index}]
   ["/health" {:action #'health/alive}]
   ["/assets/*" (ring/create-resource-handler {:path "/"})]
   ["/api" {:action #'funicular/handler}]])
