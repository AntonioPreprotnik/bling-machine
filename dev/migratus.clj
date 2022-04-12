(ns migratus
  (:require [app.config :as config]
            [app.core :as core]
            [framework.db.core :as db]
            [migratus.core :as migratus]
            [next.jdbc :as next-jdbc]))

(def cfg
  (let [c (config/load-config core/app-config)]
    (-> c
        :xiana/migration
        (assoc :db (:xiana/postgresql c)))))

(defn create-migration
  "Creates a pair (down&up) of new migration files."
  [name]
  (migratus/create cfg name))

(defn purge-db [cfg]
  (let [dbname (-> cfg :xiana/postgresql :dbname)]
    (with-open [con (-> cfg
                        (assoc-in [:xiana/postgresql :dbname] "postgres")
                        db/connect
                        (get-in [:xiana/postgresql :datasource])
                        next-jdbc/get-connection)]
      (doseq [q [(str "DROP DATABASE IF EXISTS " dbname ";")
                 (str "CREATE DATABASE " dbname ";")]]
        (next-jdbc/execute! con [q])))))

(comment

  (migratus/migrate cfg) ; applies all new migrations

  (migratus/reset cfg) ; applies all "down" migrations, then applies all "up"s

  (migratus/completed-list cfg)

  (migratus/rollback cfg)

  (migratus/up cfg
               #_" ^^^ int ids here (no vector needed)")
  (migratus/down cfg
                 #_" ^^^ int ids here (no vector needed)")

  ;; (do (purge-db) (m/migrate cfg))

  (-> (config/load-config core/app-config) db/connect purge-db)

  #_{:clj-kondo/ignore [:invalid-arity]}
  (create-migration
   #_mn
   #_"^^^ use your IDE's `eval at context` functionality  ^^^"))
