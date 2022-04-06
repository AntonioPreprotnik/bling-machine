(ns app.config
  (:require [app.web :refer [routes controller-interceptors]]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [com.walmartlabs.dyn-edn :refer [env-readers]]
            [xiana.commons :refer [deep-merge]]))

(def app-config
  {:routes                  routes
   :controller-interceptors controller-interceptors})

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
