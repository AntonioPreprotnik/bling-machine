(ns app.backend.domain.interceptors
  (:require
   [app.backend.domain.user :as user]
   [buddy.sign.jwt :as jwt]))

(defn set-current-user
  "Reads user-id from JWT(if exists) claims, fetches user and saves it into
   :current-user key in request context."
  [ctx]
  (if-let [jwt (get-in ctx [:request :data :app/jwt])]
    (let [penkala (get-in ctx [:request :penkala])
          jwt-secret (get-in ctx [:request :auth :env :jwt-secret])
          jwt-data (jwt/decrypt jwt jwt-secret)
          user-id (get-in jwt-data [:user :id])
          user (user/get-one-by-id penkala user-id)]
      (assoc-in ctx [:request :current-user] user))
    ctx))
