(ns config) (ns app.config
              (:require [app.web.router :as router]
                        [clojure.edn :as edn]
                        [clojure.java.io :as io]
                        [com.verybigthings.funicular.transit :as funicular-transit]
                        [com.walmartlabs.dyn-edn :refer [env-readers]]
                        [framework.interceptor.core :as interceptors]
                        [muuntaja.core :as muntaja]
                        [muuntaja.interceptor :as interceptor]
                        [xiana.commons :refer [deep-merge]]))

(def muuntaja-instance
  (muntaja/create
   (-> muntaja/default-options
       (assoc-in [:formats "application/transit+json" :decoder-opts] funicular-transit/read-handlers)
       (assoc-in [:formats "application/transit+json" :encoder-opts] funicular-transit/write-handlers))))

(def app-config
  {:routes                  router/routes
   :controller-interceptors [(interceptors/muuntaja (interceptor/format-interceptor muuntaja-instance))
                             interceptors/params]})

(defn- read-common-config []
  (->> (io/resource "default.edn")
       (slurp)
       (edn/read-string {:readers (env-readers)})))

(defn- read-env-config []
  (->> (io/resource "config.edn")
       (slurp)
       (edn/read-string {:readers (env-readers)})))

(defn load-config []
  (deep-merge (read-common-config)
              (read-env-config)
              app-config))
