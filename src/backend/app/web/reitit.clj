
(ns app.web.reitit
  (:require [clojure.walk :as walk]
            [com.verybigthings.funicular.transit :as funicular-transit]
            [muuntaja.core :as m]
            [reitit.coercion.spec :as coercion.spec]
            [reitit.ring :as ring]
            [reitit.ring.coercion :as ring.coercion]
            [reitit.ring.middleware.muuntaja :as muuntaja]))

(def muuntaja-instance
  (m/create
   (-> m/default-options
       (assoc-in [:formats "application/transit+json" :decoder-opts] funicular-transit/read-handlers)
       (assoc-in [:formats "application/transit+json" :encoder-opts] funicular-transit/write-handlers))))

(def opts
  {:muuntaja muuntaja-instance
   :coercion coercion.spec/coercion
   :middleware [muuntaja/format-middleware
                ring.coercion/coerce-exceptions-middleware
                ring.coercion/coerce-request-middleware
                ring.coercion/coerce-response-middleware]})

(defn- resolve-symbol [x]
  (if-let [var (and (symbol? x) (resolve x))]
    (var-get var)
    x))

(defn init [routes]
  (ring/router (walk/postwalk resolve-symbol routes) {:data opts}))

(defn reset
  "Update routes."
  [config]
  (update config :routes init))
