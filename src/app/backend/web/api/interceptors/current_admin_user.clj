(ns app.backend.web.api.interceptors.current-admin-user
  (:require
   [app.backend.domain.user :as user]
   [buddy.sign.jwt :as jwt]))

(defn set-current-user
  [ctx]
  (if-let [jwt (get-in ctx [:request :data :jwt])]
    (let [penkala (get-in ctx [:request :penkala])
          jwt-secret (get-in ctx [:request :auth :env :jwt-secret])
          jwt-data (jwt/decrypt jwt jwt-secret)
          user-id (get-in jwt-data [:user :id])
          user (user/get-one-by-id penkala user-id)]
      (assoc-in ctx [:request :current-user] user))
    ctx))
