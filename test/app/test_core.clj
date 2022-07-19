(ns app.test-core
  (:require
   [app.backend.config :as config]
   [app.backend.core :refer [app-config]]
   [app.backend.domain.auth :as auth]
   [app.backend.funicular :as funicular]
   [app.backend.penkala :as penkala]
   [framework.db.core :as db]
   [piotr-yuxuan.closeable-map :refer [closeable-map]]))

(def state* (atom nil))

(defn ->test-system []
  (-> (config/load-config app-config)
      auth/init
      db/connect
      db/migrate!
      penkala/init
      funicular/init
      closeable-map))

(defn get-system []
  (:system @state*))

(defn start-test-system
  "Kaocha pre-test hook"
  [suite _test-plan]
  (reset! state* {:system (->test-system)
                  :unique-integer 0})
  suite)

(defn stop-test-system
  "Kaocha post-test hook"
  [suite _test-plan]
  (.close (:system @state*))
  (reset! state* nil)
  suite)
