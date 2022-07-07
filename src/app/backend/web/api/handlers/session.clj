(ns app.backend.web.api.handlers.session
  (:require
   [app.backend.domain.user :as user]
   [buddy.hashers :as hashers]
   [com.verybigthings.funicular.anomalies :as anomalies]))

(def ^:private admin-not-found-error
  (-> "The username or password you entered is incorrect"
      (anomalies/not-found)
      (anomalies/->ex-info)))

(defn login [config]
  (let [{:keys [penkala data]} config
        {:keys [password]} data
        admin (user/get-admin-by-credentials penkala data)
        {:users/keys [password-hash]} admin
        is-password-valid? (hashers/check password password-hash)]
    (if (and admin is-password-valid?) admin admin-not-found-error)))
