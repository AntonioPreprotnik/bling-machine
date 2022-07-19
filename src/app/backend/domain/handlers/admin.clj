(ns app.backend.domain.handlers.admin
  (:require
   [app.backend.domain.user :as user]
   [buddy.hashers :as hashers]
   [clojure.set :refer [rename-keys]]))

(defn create-user [config]
  (let [{:keys [penkala data]} config
        user-data (rename-keys data {:password :password-hash})
        user-data-with-hash-password (update user-data :password-hash hashers/derive)]
    (user/insert penkala user-data-with-hash-password)))

(defn update-user [config]
  (let [{:keys [penkala data]} config
        user-data (:data data)
        user-id (:user-id data)]
    (user/update-by-id! penkala user-data user-id)))

(defn get-users [config]
  (let [{:keys [penkala]} config
        users (user/get-all-users penkala)]
    (if users users [])))

(defn get-user [config]
  (let [{:keys [penkala data]} config
        user-id (:user-id data)]
    (user/get-one-by-id penkala user-id)))

(defn delete-user [config]
  (let [{:keys [penkala data]} config]
    (user/delete-by-id! penkala (:user-id data))))

(defn get-current-user [{:keys [current-user]}]
  current-user)
