(ns pasta-xiana.api.handlers.user
  (:require
    [pasta-xiana.boundary.user :as user]
    [medley.core :as m]))

(defn create [config]
  (let [{:keys [penkala data]} config
        uuid (m/random-uuid)
        user-data (assoc data :id uuid)]
    (user/insert penkala user-data)))

(defn update [config]
  (let [{:keys [penkala data]} config
        user-data (:data data)
        user-id (:user-id data)]
    (user/update-by-id! penkala user-data user-id)))

(defn list [config]
  (let [{:keys [penkala]} config
        users (user/get-all-users penkala)]
    (if users users [])))

(defn one [config]
  (let [{:keys [penkala data]} config
        user-id (:user-id data)]
    (user/get-one-by-id penkala user-id)))
