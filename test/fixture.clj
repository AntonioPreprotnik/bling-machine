(ns fixture
  (:require
    [app.core :refer [->system app-cfg]]
    [config.core :refer [load-env]]
    [migratus.core :as migratus]
    [next.jdbc :as nj])
  (:import io.zonky.test.db.postgres.embedded.EmbeddedPostgres))

(def state* (atom nil))

(def config {})

(defn get-system []
   (-> state* deref :system))

(def db-config
  {:port     54321
   :db-name  "postgres"
   :user     "postgres"
   :password "postgres"})

(defn make-jdbc-url [{:keys [port db-name user password]}]
  (str "jdbc:postgresql://localhost:" port "/" db-name "?user=" user "&password=" password))

(defn apply-mocks [system mock]
  (reduce-kv
    (fn [system' key mocked-key]
      (let [key-val (get system' key)]
        (-> system'
          (dissoc key)
          (assoc mocked-key key-val))))
    system
    mock))

(defn init-system! [init mock]
  (->system (merge app-cfg config)))

(defn start-pg! []
  (-> (EmbeddedPostgres/builder)
    (.setServerConfig "fsync" "off")
    (.setServerConfig "full_page_writes" "off")
    (.setPort (:port db-config))
    (.start)))

(defn stop-pg! [pg]
  (.close pg))

(defn create-db-snapshot! []
  (let [db-name (:db-name db-config)
        db-snapshot-name (str db-name "_snapshot")
        jdbc-url (-> db-config (dissoc :db-name) make-jdbc-url)
        connection (nj/get-connection jdbc-url)]
    (nj/execute! connection ["SELECT pg_terminate_backend(pg_stat_activity.pid) FROM pg_stat_activity WHERE datname = ? AND pid <> pg_backend_pid()" db-name])
    (nj/execute! connection [(str "CREATE DATABASE " db-snapshot-name " TEMPLATE " db-name)])))

(defn restore-db-snapshot! []
  (let [jdbc-url (make-jdbc-url (assoc db-config :db-name "template1"))
        connection (nj/get-connection jdbc-url)
        db-name (:db-name db-config)
        db-snapshot-name (str db-name "_snapshot")]
    (nj/execute! connection [(str "DROP DATABASE " db-name " WITH (FORCE)")])
    (nj/execute! connection [(str "CREATE DATABASE " db-name " TEMPLATE " db-snapshot-name)])))

(defn with-system!
  ([test-fn] (with-system! nil test-fn))
  ([{:keys [init mock]} test-fn]
   (try
     (let [pg (start-pg!)
           env (load-env)
           db-config (:framework.db.storage/postgresql env)
           migration-cnf (:framework.db.storage/migration env)
           mig-config (assoc migration-cnf
                        :db (nj/get-datasource db-config))
           migration (migratus/migrate mig-config)]
       (create-db-snapshot!)
       (reset! state* {:pg pg :system (init-system! init mock)})
       (test-fn))
     (catch Exception e
       (do
         (.printStackTrace e)
         (throw e)))
     (finally
       (when-let [state @state*]
         (.close (:system @state*))
         (stop-pg! (:pg state))
         (reset! state* nil))))))

(defn with-reset-db! [test-fn]
  (try
    (test-fn)
    (catch Exception e
      (do
        (.printStackTrace e)
        (throw e)))
    (finally
      (restore-db-snapshot!))))

(defn with-before! [after-fns test-fn]
  (let [system (get-system)]
    (doseq [afn after-fns]
      (afn system)))
  (test-fn))

(defn with-after! [after-fns test-fn]
  (test-fn)
  (let [system (get-system)]
    (doseq [afn after-fns]
      (afn system))))

