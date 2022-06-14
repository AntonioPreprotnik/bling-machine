(ns shared.schema
  (:require
   [malli.core :as m]
   [malli.error :as me]
   [malli.util :as mu]
   shared.schema.admin
   shared.schema.common
   shared.schema.user))

(def registry
  (merge
   (m/default-schemas)
   (mu/schemas)
   shared.schema.common/registry
   shared.schema.user/registry
   shared.schema.admin/registry))

(defn validate
  "Validates data over schema in registry."
  [data schema]
  (if-let [errors (->> data
                       (m/explain (schema registry))
                       (me/humanize))]
    (throw (ex-info "Invalid data" {:data data :errors errors}))
    data))
