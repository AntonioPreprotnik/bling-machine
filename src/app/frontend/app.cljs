(ns app.frontend.app
  (:require
   [app.frontend.controllers.admin.login-form]
   [app.frontend.controllers.admin.users.create-user]
   [app.frontend.controllers.admin.users.delete-user]
   [app.frontend.controllers.admin.users.edit-user]
   [app.frontend.controllers.admin.users.selected-user]
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

(def admin-app
  {:keechma.app/should-run? (is-role :admin)
   :keechma.app/deps [:role]
   :keechma/controllers
   {:create-user
    #:keechma.controller {:params (fn [{:keys [router modal-add-user]}]
                                    (and modal-add-user (= "users" (:subpage router))))
                          :deps   [:router :modal-add-user :switch-admin-role]}
    :selected-user
    #:keechma.controller {:params (fn [{:keys [router users]}]
                                    (and users (= "users" (:subpage router))))
                          :deps   [:router :users]}
    :edit-user
    #:keechma.controller {:params (fn [{:keys [router modal-edit-user selected-user]}]
                                    (when (and modal-edit-user (= "admin" (:page router)))
                                      selected-user))
                          :deps   [:router :selected-user :modal-edit-user]}
    :delete-user
    #:keechma.controller {:params (fn [{:keys [router modal-delete-user selected-user]}]
                                    (when (and modal-delete-user (= "admin" (:page router)))
                                      selected-user))
                          :deps   [:router :selected-user :modal-delete-user]}
    :switch-admin-role
    #:keechma.controller {:type                       :generic/switch
                          :params                     (fn [{:keys [router]}]
                                                        (= "admin" (:page router)))
                          :deps                       [:router]
                          :generic.switch/set-default (fn [_ _ _] true)}}})

(def app
  (->
   {:keechma.subscriptions/batcher rdom/unstable_batchedUpdates
    :keechma/controllers
    {:router            #:keechma.controller {:params                   true
                                              :type                     :keechma/router
                                              :keechma.router/base-href "/"
                                              :keechma/routes           routes}

     :entitydb          #:keechma.controller {:params                  true
                                              :type                    :keechma/entitydb
                                              :keechma.entitydb/schema {:user {:entitydb/id :users/id}}}

     :jwt               #:keechma.controller {:params true
                                              :deps   [:router]}

     :role              #:keechma.controller {:params (fn [{:keys [jwt]}]
                                                        (if jwt :admin :anon))
                                              :type   :keechma/subscription
                                              :deps   [:jwt]}

     :modal-add-user    #:keechma.controller {:type                       :generic/switch
                                              :params                     (fn [{:keys [router]}]
                                                                            (= "admin" (:page router)))
                                              :deps                       [:router]
                                              :generic.switch/set-default (fn [_ _ _] true)}

     :modal-edit-user   #:keechma.controller {:type                       :generic/switch
                                              :params                     (fn [{:keys [router]}]
                                                                            (= "admin" (:page router)))
                                              :deps                       [:router]
                                              :generic.switch/set-default (fn [_ _ _] true)}

     :modal-delete-user #:keechma.controller {:type                       :generic/switch
                                              :params                     (fn [{:keys [router]}]
                                                                            (= "admin" (:page router)))
                                              :deps                       [:router]
                                              :generic.switch/set-default (fn [_ _ _] true)}

     :users             #:keechma.controller {:params (fn [{:keys [router]}]
                                                        (= "admin" (:page router)))
                                              :deps   [:entitydb :router]}}

    :keechma/apps {:admin admin-app
                   :anon {:keechma.app/should-run? (is-role :anon)
                          :keechma.app/deps [:role]
                          :keechma/controllers
                          {:login-form
                           #:keechma.controller {:params (fn [{:keys [router]}]
                                                           (= "home" (:page router)))
                                                 :deps   [:router]}}}}}
   f/install))
