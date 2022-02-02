(ns app.app
  (:require [keechma.next.controllers.router]
            [keechma.next.controllers.dataloader]
            [keechma.next.controllers.subscription]
            [keechma.next.controllers.entitydb]
            [app.controllers.current-user]
            [com.verybigthings.funicular.controller :as f]
            ["react-dom" :as rdom]))
(def app
  (->
   {:keechma.subscriptions/batcher rdom/unstable_batchedUpdates
    :keechma/controllers
    {:router {:keechma.controller/params true
              :keechma.controller/type :keechma/router
              :keechma/routes [["" {:page "home"}] ":page" ":page/:subpage"]}
     :dataloader {:keechma.controller/params true
                  :keechma.controller/type :keechma/dataloader}
     :entitydb {:keechma.controller/params true
                :keechma.controller/type :keechma/entitydb
                :keechma.entitydb/schema {:user {:entitydb/id :users/id}}}
     :current-user #:keechma.controller{:params true
                                        :deps [:entitydb]}}}
   f/install))