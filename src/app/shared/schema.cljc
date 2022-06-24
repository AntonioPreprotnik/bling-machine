(ns app.shared.schema
  (:require
   app.shared.schema.admin
   app.shared.schema.common
   app.shared.schema.user
   [malli.core :as m]
   [malli.error :as me]
   [malli.util :as mu]))

(def registry
  (merge
   (m/default-schemas)
   (mu/schemas)
   app.shared.schema.common/registry
   app.shared.schema.user/registry
   app.shared.schema.admin/registry))

(defn validate
  "Validates data over schema in registry."
  [data schema]
  (if-let [errors (->> data
                       (m/explain (schema registry))
                       (me/humanize))]
    (throw (ex-info "Invalid data" {:data data :errors errors}))
    data))
