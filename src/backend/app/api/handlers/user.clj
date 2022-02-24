(ns app.api.handlers.user
  (:require
   [app.boundary.user :as user]
   [medley.core :as m]))

(defn create-one [config]
  (let [{:keys [penkala data]} config
        uuid (m/random-uuid)
        user-data (assoc data :id uuid)]
    (user/insert penkala user-data)))

(defn update-one [config]
  (let [{:keys [penkala data]} config
        user-data (:data data)
        user-id (:user-id data)]
    (user/update-by-id! penkala user-data user-id)))

(defn get-all [config]
  (let [{:keys [penkala]} config
        users (user/get-all-users penkala)]
    (if users users [])))

(defn get-one [config]
  (let [{:keys [penkala data]} config
        user-id (:user-id data)]
    #_(trace>> ::UserDB [user/UserDatabase penkala])
    (user/get-one-by-id penkala user-id)))
