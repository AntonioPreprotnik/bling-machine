(ns app.backend.web.api.handlers.user
  (:require
   [app.backend.domain.user :as user]))

(defn create-one [config]
  (let [{:keys [penkala data]} config]
    (user/insert penkala data)))

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
    (user/get-one-by-id penkala user-id)))

(defn delete-by-id [config]
  (let [{:keys [penkala data]} config
        user-id data]
    (user/delete-by-id! penkala user-id)))