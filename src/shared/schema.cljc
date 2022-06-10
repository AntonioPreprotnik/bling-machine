(ns schema
  (:require
   [lambdaisland.regal :as regal]
   [malli.core :as m]
   [malli.error :as me]
   [malli.util :as mu]
   schema.common
   [schema.user]))

(def registry
  (merge
   (m/default-schemas)
   (mu/schemas)
   schema.common/registry
   schema.user/registry
   {:password.rules/length
    [:string {:min 8
              :error/message "Must be 8 characters"}]
    :app.input.login
    [:map
     [:email ::email]
     [:password :password.rules/length]]}))

(defn validate
  "Validates data over schema in registry."
  [data schema]
  (if-let [errors (->> data
                       (m/explain (schema registry))
                       (me/humanize))]
    (throw (ex-info "Invalid data" {:data data :errors errors}))
    data))
