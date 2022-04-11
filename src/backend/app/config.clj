(ns app.config
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [com.walmartlabs.dyn-edn :refer [env-readers]]
            [xiana.commons :refer [deep-merge]]))

(defn- read-default-config []
  (->> (io/resource "default.edn")
       (slurp)
       (edn/read-string)))

(defn- read-env-config []
  (->> (io/resource "config.edn")
       (slurp)
       (edn/read-string {:readers (env-readers)})))

(defn load-config [app-config]
  (deep-merge (read-default-config)
              (read-env-config)
              app-config))
