(ns todo-app.core
  (:require
    [todo-app.controllers.index :as index]
    [todo-app.controllers.re-frame :as re-frame]
    [framework.config.core :as config]
    [framework.db.core :as db]
    [framework.db.seed :as seed]
    [framework.interceptor.core :as interceptors]
    [framework.rbac.core :as rbac]
    [framework.route.core :as routes]
    [framework.session.core :as session]
    [framework.webserver.core :as ws]
    [piotr-yuxuan.closeable-map :refer [closeable-map]]
    [reitit.ring :as ring]
    [com.verybigthings.funicular.core :as f]
    ;[app.funicular :as api]
    [com.verybigthings.penkala.next-jdbc :refer [select!]]
    [state :as st]
    [xiana.commons :refer [rename-key]]
    [tdebug :refer [trace> trace>>]]
    [reveal
     :refer
     [add-tap-rui open-snapshot]
     :as rui]
    [xiana.core]))

(defn view
  [{{db-data :db-data} :response-data :as state}]
  (xiana.core/ok (assoc state :response {:status 200
                                         :body (mapv :todos/label db-data)})))
(defn fetch
  [state]
  ;(trace>> :state state :deps)
  (xiana.core/ok (assoc state
                   :view view
                   :query {:select [:*] :from [:todos]})))

(def routes
  [["/todos" {:action #'re-frame/handle-index}]
   ["/assets/*" (ring/create-resource-handler {:path "/"})]
   ["/api" {}
    ["/todos" {:get {:action #'fetch}}]]])

(defn ->system
  [app-cfg]
  (-> (config/config app-cfg)
      (rename-key :framework.app/auth :auth)
      routes/reset
      rbac/init
      session/init-backend
      db/connect
      db/migrate!
      seed/seed!
      (trace> ::system)
      ws/start
      closeable-map))

(def app-cfg
  {:routes routes
   :router-interceptors     []
   :controller-interceptors [(interceptors/muuntaja)
                             interceptors/params
                             session/guest-session-interceptor
                             interceptors/view
                             interceptors/side-effect
                             db/db-access
                             rbac/interceptor]})

(defn -main
  [& _args]
  (->system app-cfg))
