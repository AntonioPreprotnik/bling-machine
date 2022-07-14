(ns app.backend.domain.auth
  (:require
   [buddy.sign.jwt :as jwt]
   [tick.alpha.api :as tick]))

(defrecord Boundary [env])

(defn make-jwt [{:keys [env]} user-id]
  (let [claims {:user {:id user-id}
                :exp (tick/>> (tick/now) (tick/new-duration 24 :hours))}]
    (jwt/encrypt claims (:jwt-secret env))))

(defn decrypt-jwt [{:keys [env]} jwt]
  (jwt/decrypt jwt (:jwt-secret env)))

(defn init [{:keys [auth] :as config}]
  (assoc config :auth (->Boundary (select-keys auth [:jwt-secret]))))
