(ns app.boundary.user
  (:require [com.verybigthings.penkala.relation :as r]
            [app.penkala :refer [insert! select! select-one! update!]]
            [medley.core :refer [remove-vals]]
            [tdebug :refer [trace> trace>>]]))

(defprotocol UserDatabase
  (insert [penkala data])
  (update-by-id! [penkala data id])
  (get-all-users [penkala])
  (get-one-by-id [penkala id]))

(extend-protocol UserDatabase
  app.penkala.Boundary

  (insert [{:keys [env]} data]
    (insert! env :users data))

  (update-by-id! [{:keys [env]} data id]
    (let [updateable (-> (:users env)
                         r/->updatable
                         (r/where [:= :id [:cast id "uuid"]]))]
      (-> (update! env updateable data) first)))

  (get-all-users [{:keys [env]}]
    (select! env :users))

  (get-one-by-id [{:keys [env]} id]
    (let [users (-> (:users env)
                    (r/where [:= :id [:cast id "uuid"]]))]
      ;(trace>> ::by-id users)
      (select-one! env users))))
