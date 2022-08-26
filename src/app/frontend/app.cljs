(ns app.frontend.app
  (:require
   [app.frontend.controllers.admin.current-admin]
   [app.frontend.controllers.currencies.currencies]
   [app.frontend.controllers.generic.switch]
   [app.frontend.controllers.jwt]
   [app.frontend.controllers.users]
   [com.verybigthings.funicular.controller :as f]
   [keechma.next.controllers.entitydb]
   [keechma.next.controllers.router]
   [keechma.next.controllers.subscription]
   [react-dom :as rdom]))

(def routes
  [["" {:page "home"}]
   ":page" ":page/:subpage"])

(defn is-role [role] (fn [deps] (= role (:role deps))))

(def app
  (->
   {:keechma.subscriptions/batcher rdom/unstable_batchedUpdates
    :keechma/controllers
    {:router
     #:keechma.controller {:params                   true
                           :type                     :keechma/router
                           :keechma.router/base-href "/"
                           :keechma/routes           routes}

     :entitydb
     #:keechma.controller {:params                  true
                           :type                    :keechma/entitydb
                           :keechma.entitydb/schema {:user {:entitydb/id :users/id}
                                                     :currency {:entitydb/id :currencies/id}}}

     :jwt
     #:keechma.controller {:params true}

     :current-admin
     #:keechma.controller {:params #(:jwt %)
                           :deps   [:jwt]}

     :role
     #:keechma.controller {:params (fn [{:keys [jwt current-admin]}]
                                     (let [{:users/keys [is-admin]} current-admin
                                           is-jwt (boolean (seq jwt))]
                                       (if (and is-jwt is-admin) :admin :anon)))
                           :type   :keechma/subscription
                           :deps   [:jwt :current-admin]}}

    :keechma/apps {:currencies   {:keechma.app/should-run? (is-role :anon)
                                  :keechma.app/deps [:role]
                                  :keechma/controllers
                                  {:currencies
                                   #:keechma.controller {:params (fn [{:keys [router]}]
                                                                   (= "home" (:page router)))
                                                         :deps   [:router]}}}}}
   f/install))
