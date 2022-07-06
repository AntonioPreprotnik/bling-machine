(ns app.backend.web.api.handlers.session
  (:require
   [app.backend.domain.user :as user]
   [com.verybigthings.funicular.anomalies :as anomalies]))

(def ^:private admin-not-found-error
  (-> "The username or password you entered is incorrect"
      (anomalies/not-found)
      (anomalies/->ex-info)))

(defn login [config]
  (let [{:keys [penkala data]} config
        admin (user/get-admin-by-credentials penkala data)]
    (if admin admin admin-not-found-error)))
