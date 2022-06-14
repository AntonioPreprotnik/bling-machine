(ns test-core
  (:require
   [backend.config :as config]
   [backend.core :refer [app-config]]
   [backend.funicular :as funicular]
   [backend.penkala :as penkala]
   [framework.db.core :as db]
   [piotr-yuxuan.closeable-map :refer [closeable-map]]))

(def state* (atom nil))

(defn ->test-system []
  (-> (config/load-config app-config)
      db/connect
      db/migrate!
      penkala/init
      funicular/init
      closeable-map))

(defn get-system []
  (-> state* deref :system))

(defn start-test-system
  "Kaocha pre-test hook"
  [suite _test-plan]
  (reset! state* {:system (->test-system)})
  suite)

(defn stop-test-system
  "Kaocha post-test hook"
  [suite _test-plan]
  (.close (:system @state*))
  (reset! state* nil)
  suite)
