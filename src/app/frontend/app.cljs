(ns frontend.app
  (:require
   [com.verybigthings.funicular.controller :as f]
   frontend.controllers.admin.login-form
   frontend.controllers.current-user
   frontend.controllers.users
   keechma.next.controllers.dataloader
   keechma.next.controllers.entitydb
   keechma.next.controllers.router
   keechma.next.controllers.subscription
   [react-dom :as rdom]))

(def app
  (->
   {:keechma.subscriptions/batcher rdom/unstable_batchedUpdates
    :keechma/controllers
    {:router {:keechma.controller/params true
              :keechma.controller/type :keechma/router
              :keechma/routes [["" {:page "home"}] ":page" ":page/:id"]}
     :dataloader {:keechma.controller/params true
                  :keechma.controller/type :keechma/dataloader}
     :entitydb #:keechma.controller{:params true
                                    :type :keechma/entitydb
                                    :keechma.entitydb/schema {:user {:entitydb/id :users/id}}}
     :login-form #:keechma.controller {:params (fn [{:keys [router]}]
                                                 (= "home" (:page router)))
                                       :deps [:router]}
     :users #:keechma.controller{:params (fn [{:keys [router]}]
                                           (= "admin-panel" (:page router)))
                                 :deps [:entitydb :router]}
     :current-user #:keechma.controller{:params (fn [{:keys [router]}]
                                                  (when (= "admin-panel" (:page router))
                                                    (:id router)))
                                        :deps [:router :entitydb]}}}
   f/install))
