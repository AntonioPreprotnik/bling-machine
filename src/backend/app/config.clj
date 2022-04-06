(ns app.config
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [com.walmartlabs.dyn-edn :refer [env-readers]]
            [xiana.commons :refer [deep-merge]]))

(defn- read-common-config []
  (->> (io/resource "default.edn")
       (slurp)
       (edn/read-string {:readers (env-readers)})))

(defn- read-env-config []
  (->> (io/resource "config.edn")
       (slurp)
       (edn/read-string {:readers (env-readers)})))

(defn load-config [app-config]
  (deep-merge (read-common-config)
              (read-env-config)
              app-config))
