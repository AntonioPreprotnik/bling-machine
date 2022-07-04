(ns app.backend.domain.user
  (:require
   [app.backend.boundary.penkala-helpers :refer [cast-as]]
   [app.backend.penkala :refer [delete! insert! select! select-one! update!]]
   [com.verybigthings.penkala.relation :as r]))

(defprotocol UserDatabase
  (insert [penkala data])
  (update-by-id! [penkala data id])
  (get-all-users [penkala])
  (get-one-by-id [penkala id])
  (delete-by-id! [penkala id])
  (get-admin-by-credentials [penkala data]))

(extend-protocol UserDatabase
  app.backend.penkala.Boundary

  (insert [{:keys [env]} data]
    (insert! env :users data))

  (update-by-id! [{:keys [env]} data id]
    (let [updateable (-> (:users env)
                         r/->updatable
                         (r/where [:= :id (cast-as id "uuid")]))]
      (-> (update! env updateable data) first)))

  (get-all-users [{:keys [env]}]
    (select! env :users))

  (get-one-by-id [{:keys [env]} id]
    (let [users (-> (:users env)
                    (r/where [:= :id [:cast id "uuid"]]))]
      (select-one! env users)))

  (delete-by-id! [{:keys [env]} id]
    (let [delete-user (-> (:users env)
                          r/->deletable
                          (r/where [:= :id (cast-as id "uuid")]))]
      (delete! env delete-user)))

  (get-admin-by-credentials [{:keys [env]} {:keys [email password-hash]}]
    (let [users (-> (:users env)
                    (r/where [:= :email email])
                    (r/where [:= :password-hash password-hash])
                    (r/where [:= :is-admin true]))]
      (select-one! env users))))