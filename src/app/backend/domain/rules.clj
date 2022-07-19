(ns app.backend.domain.rules)

(defn is-admin? [request]
  (get-in request [:current-user :users/is-admin]))
