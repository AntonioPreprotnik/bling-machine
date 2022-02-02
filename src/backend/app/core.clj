(ns app.core
  (:require
   [app.controllers.index :as index]
   [app.controllers.re-frame :as re-frame]
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
   [com.verybigthings.penkala.next-jdbc :refer [select!]]
   [app.penkala :as penkala]
   [app.funicular :as api]
    ;;[app.readers :refer [readers]]
   [app.handlers.funicular]
   ;;[state :as st]
   [xiana.commons :refer [rename-key]]
   [xiana.core]
   [medley.core :as m]
   [tdebug :refer [trace> trace>>]]
   [reveal
    :refer
    [add-tap-rui open-snapshot]
    :as rui]
   [clojure.edn :as edn]))

;(defn view
;  [{{db-data :db-data} :response-data :as state}]
;  (xiana.core/ok (assoc state :response {:status 200
;                                         :body (mapv :todos/label db-data)})))
;(defn fetch
;  [state]
;  (trace>> :state state :deps)
;  (xiana.core/ok (assoc state
;                   :view view
;                   :query {:select [:*] :from [:todos]})))

(def routes
  [["/" {:action #'re-frame/handle-index}]
   ["/assets/*" (ring/create-resource-handler {:path "/"})]
   ["/api" {:action #'app.handlers.funicular/handler}]])

(defn ->system
  [app-cfg]
  (-> (config/config app-cfg)
      (rename-key :framework.app/auth :auth)
      routes/reset
      rbac/init
      session/init-backend
      db/connect
      penkala/init
      api/init
      db/migrate!
      seed/seed!
      ws/start
      (trace> ::system)
      closeable-map))

(def app-cfg
  {:routes                  routes
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

(comment
  (-> @st/dev-sys
      :app/funicular
      (api/execute {:queries {:user [:api.user/get-all-users {}]}}))

  (-> @st/dev-sys
      :app/funicular
      (api/execute {:command [:api.user/create {:email "adgg@vbt.com"
                                                :first-name "Frka12"
                                                :last-name "Trle12"
                                                :zip "10000"}]}))

  (-> @st/dev-sys
      :app/funicular
      (api/execute {:command [:api.user/update {:user-id #uuid"bb621b8b-a841-44c5-b393-01d4411bfb10"
                                                :data    {:email      "ad@vbt.com"
                                                          :first-name "Frka21"
                                                          :last-name  "Trle21"
                                                          :zip        "10000"}}]}))

  (-> @st/dev-sys
      :app/funicular
      (api/execute {:queries {:user [:api.user/get-one {:user-id #uuid"bb621b8b-a841-44c5-b393-01d4411bfb10"}]}})))
