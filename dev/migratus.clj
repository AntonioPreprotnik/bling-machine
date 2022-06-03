(ns migratus
  "REPL namespace used for managing local DB migrations and seeds."
  (:require
   [app.config :as config]
   [app.core :refer [app-config]]
   [framework.db.core :as db]
   [migratus.core :as migratus]
   [next.jdbc :as next-jdbc]
   [xiana.commons :refer [rename-key]]))

(defn- load-migration-config []
  (let [config (config/load-config app-config)
        db-config (:xiana/postgresql config)]
    (assoc (:xiana/migration config) :db db-config)))

(defn- load-seed-config []
  (-> (load-migration-config)
      (rename-key :seeds-dir :migration-dir)
      (rename-key :seeds-table-name :migration-table-name)))

(defn recreate-db
  "Drops down and recreates database free of data and migrations."
  []
  (let [config (config/load-config app-config)
        db-name (get-in config [:xiana/postgresql :dbname])
        master-config (assoc-in config [:xiana/postgresql :dbname] "postgres")
        datasource (get-in (db/connect master-config) [:xiana/postgresql :datasource])]
    (with-open [connection (next-jdbc/get-connection datasource)]
      (doseq [query [(format "DROP DATABASE IF EXISTS %s;" db-name)
                     (format "CREATE DATABASE %s;" db-name)]]
        (next-jdbc/execute! connection [query])))))

(defn check-db-integrity
  "Used for checking migration reversibility and seeds compatibility."
  [& _]
  (let [migration-config (load-migration-config)
        seed-config (load-seed-config)]
    (println "Reset migrations:")
    (migratus/reset migration-config)

    (println "Running seeds:")
    (migratus/reset seed-config)

    (println "Rollback migrations:")
    (migratus/rollback migration-config)))

(def ^:private down-count 1)
(def ^:private migration-config (load-migration-config))
(def ^:private seed-config (load-seed-config))
(def ^:private up-count 1)

(comment
  ;;# --------------------------------------------------------------------------
  ;;# FRESH DB
  ;;# --------------------------------------------------------------------------

  (recreate-db)

  ;;# --------------------------------------------------------------------------
  ;;# MIGRATIONS
  ;;# --------------------------------------------------------------------------
  (declare migration-name seed-name)

  (migratus/create migration-config migration-name)
  (migratus/destroy migration-config)
  (migratus/down migration-config down-count)
  (migratus/init migration-config)
  (migratus/migrate migration-config)
  (migratus/reset migration-config)
  (migratus/rollback migration-config)
  (migratus/up migration-config up-count)

  ;;# --------------------------------------------------------------------------
  ;;# SEEDS
  ;;# --------------------------------------------------------------------------

  (migratus/create seed-config seed-name)
  (migratus/destroy seed-config)
  (migratus/migrate seed-config)
  (migratus/reset seed-config)

  ;;# --------------------------------------------------------------------------
  ;;# DB INTEGRITY
  ;;# --------------------------------------------------------------------------

  (check-db-integrity))
