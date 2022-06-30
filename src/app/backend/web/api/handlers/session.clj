(ns app.backend.web.api.handlers.session
  (:require
   [app.backend.domain.user :as user]))

(defn login [config]
  (let [{:keys [penkala data]} config]
    (user/get-admin-by-credentials penkala data)))
