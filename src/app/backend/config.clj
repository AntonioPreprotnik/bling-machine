(ns app.backend.config
  (:require
   [clojure.edn :as edn]
   [clojure.java.io :as io]
   [com.walmartlabs.dyn-edn :refer [env-readers]]
   [xiana.commons :refer [deep-merge]]))

(defn- parse-db-url [db-url]
  (re-find (re-matcher #"postgres://(\w+):(\w+)@([\w\-\.]+):(\d+)/(\w+)" db-url)))

(defn- db-url-to-args [config]
  (let [db-url (get-in config [:xiana/postgresql :dburl])
        [_ user password host port dbname] (parse-db-url db-url)]
    (assoc config :xiana/postgresql (merge (:xiana/postgresql config)
                                           {:user     user
                                            :password password
                                            :host     host
                                            :port     (Integer/parseInt port)
                                            :dbname   dbname}))))

(defn transform-db-config [config]
  (if (get-in config [:xiana/postgresql :dburl])
    (db-url-to-args config)
    config))

(defn- read-default-config []
  (->> (io/resource "common.edn")
       (slurp)
       (edn/read-string)))

(defn- read-env-config []
  (->> (io/resource "config.edn")
       (slurp)
       (edn/read-string {:readers (env-readers)})))

(defn load-config [app-config]
  (->> (deep-merge (read-default-config)
                   (read-env-config)
                   app-config)
       transform-db-config))
