(ns app.app
  (:require [keechma.next.controllers.router]
            [keechma.next.controllers.dataloader]
            [keechma.next.controllers.subscription]
            [keechma.next.controllers.entitydb]
            [app.controllers.current-user]
            [app.controllers.users]
            [com.verybigthings.funicular.controller :as f]
            ["react-dom" :as rdom]))
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
     :users #:keechma.controller{:params (fn [{:keys [router]}]
                                           (= "users" (:page router)))
                                 :deps [:entitydb :router]}
     :current-user #:keechma.controller{:params (fn [{:keys [router]}]
                                                  (when (= "users" (:page router))
                                                    (:id router)))
                                        :deps [:router :entitydb]}}}
   f/install))