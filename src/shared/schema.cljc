(ns schema
  (:require
   [malli.core :as m]
   [malli.error :as me]
   [malli.util :as mu]
   schema.admin
   schema.common
   schema.user))

(def registry
  (merge
   (m/default-schemas)
   (mu/schemas)
   schema.common/registry
   schema.user/registry
   schema.admin/registry))

(defn validate
  "Validates data over schema in registry."
  [data schema]
  (if-let [errors (->> data
                       (m/explain (schema registry))
                       (me/humanize))]
    (throw (ex-info "Invalid data" {:data data :errors errors}))
    data))
