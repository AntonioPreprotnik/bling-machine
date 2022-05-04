(ns app.db
  (:require
   [app.config :as config]
   [app.core :refer [app-config]]
   [clojure.string :as str]
   [migratus.core :as migratus]
   [xiana.commons :refer [rename-key]]))

(defn migrate-help []
  (println "Available migratus commands:")
  (prn "create"
       "destroy"
       "down"
       "init"
       "migrate"
       "reset"
       "rollback"
       "up"))

(defn- prepare-migrate-config []
  (let [config (config/load-config app-config)
        db-config (:xiana/postgresql config)]
    (assoc (:xiana/migration config) :db db-config)))

(defn- prepare-seed-config []
  (-> (prepare-migrate-config)
      (rename-key :seeds-dir :migration-dir)
      (rename-key :seeds-table-name :migration-table-name)))

(defn migrate [& args]
  (let [[command name type] args
        [_ & ids] args
        config (prepare-migrate-config)]
    (if (str/blank? command)
      (migrate-help)
      (case (str/lower-case command)
        "create" (migratus/create config name (keyword type))
        "destroy" (migratus/destroy config)
        "down" (apply migratus/down config (map #(Long/parseLong %) ids))
        "init" (migratus/init config)
        "migrate" (migratus/migrate config)
        "reset" (migratus/reset config)
        "rollback" (migratus/rollback config)
        "up" (apply migratus/up config (map #(Long/parseLong %) ids))
        (migrate-help)))))

(defn migrator [{:keys [:args]}]
  (apply migrate args))

(defn seed [& args]
  (let [[command name type] args
        config (prepare-seed-config)]
    (if (and (:migration-dir config) (:migration-table-name config))
      (case command
        "create" (migratus/create config name (keyword type))
        "reset" (migratus/reset config)
        "destroy" (migratus/destroy config)
        "migrate" (migratus/migrate config)
        (println "You can 'create' 'reset' 'destroy' or 'migrate' your seed data"))
      (println "No seed configuration found"))))

(defn seeder [{:keys [:args]}]
  (apply seed args))
