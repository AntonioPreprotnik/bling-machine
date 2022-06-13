(ns app.app
  (:require
   app.controllers.admin.login-form
   app.controllers.admin.users.create-user
   app.controllers.current-user
   app.controllers.generic.switch
   app.controllers.users
   [com.verybigthings.funicular.controller :as f]
   keechma.next.controllers.entitydb
   keechma.next.controllers.router
   [react-dom :as rdom]))

(def routes
  [["" {:page "home"}]
   ":page" ":page/:subpage"])

(def app
  (->
   {:keechma.subscriptions/batcher rdom/unstable_batchedUpdates
    :keechma/controllers
    {:router       #:keechma.controller {:params                   true
                                         :type                     :keechma/router
                                         :keechma.router/base-href "/"
                                         :keechma/routes           routes}

     :entitydb     #:keechma.controller {:params                  true
                                         :type                    :keechma/entitydb
                                         :keechma.entitydb/schema {:user {:entitydb/id :users/id}}}

     :login-form   #:keechma.controller {:params (fn [{:keys [router]}]
                                                   (= "home" (:page router)))
                                         :deps   [:router]}

     :create-user #:keechma.controller {:params (fn [{:keys [router modal-add-user]}]
                                                  (and modal-add-user (= "users" (:subpage router))))
                                        :deps   [:router :modal-add-user]}

     :modal-add-user #:keechma.controller {:type                       :generic/switch
                                           :params                     (fn [{:keys [router]}]
                                                                         (= "admin" (:page router)))
                                           :deps                       [:router]
                                           :generic.switch/set-default (fn [_ _ _] true)}

     :modal-edit-user #:keechma.controller {:type                       :generic/switch
                                            :params                     (fn [{:keys [router]}]
                                                                          (= "admin" (:page router)))
                                            :deps                       [:router]
                                            :generic.switch/set-default (fn [_ _ _] true)}

     :users        #:keechma.controller {:params (fn [{:keys [router modal-add-user]}]
                                                   (and (not modal-add-user) (= "admin" (:page router))))
                                         :deps   [:entitydb :router :modal-add-user]}

     :current-user #:keechma.controller {:params (fn [{:keys [router]}]
                                                   (when (= "admin" (:page router))
                                                     (:id router)))
                                         :deps   [:router :entitydb]}}}
   f/install))
