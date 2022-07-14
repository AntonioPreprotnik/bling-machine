(ns app.backend.web.api.handlers.session
  (:require
   [app.backend.domain.auth :refer [decrypt-jwt make-jwt]]
   [app.backend.domain.user :as user]
   [buddy.hashers :as hashers]
   [com.verybigthings.funicular.anomalies :as anomalies]))

(def ^:private admin-not-found-error
  (-> "The username or password you entered is incorrect"
      (anomalies/not-found)
      (anomalies/->ex-info)))

(defn login [{:keys [data penkala auth]}]
  (let [{:keys [password]} data
        admin (user/get-admin-by-credentials penkala data)
        {:users/keys [password-hash id]} admin
        is-password-valid? (hashers/check password password-hash)]
    (if (and admin is-password-valid?)
      {:current-admin-data admin
       :jwt (make-jwt auth id)}
      admin-not-found-error)))

(defn check-jwt [{:keys [data penkala auth]}]
  (try
    (let [claims (decrypt-jwt auth data)
          user-id (get-in claims [:user :id])
          user (user/get-one-by-id penkala user-id)]
      (if user :valid :invalid))
    (catch Exception _e :expired)))
