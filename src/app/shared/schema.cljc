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
   {:currencies/id [:uuid]
    :currencies/currency-name  [:string]
    :currencies/exchange-rate [:double]
    :currencies/creation-date  [:string]

    :app/currency
    [:map
     :currencies/id
     :currencies/currency-name
     :currencies/exchange-rate
     :currencies/creation-date]

    :app/currencies
    [:vector :app/currency]

    :app/unique-currencies
    [:vector [:map [:currencies/currency-name]]]

    :app.input.currency/create
    [:map
     :currencies/currency-name
     :currencies/exchange-rate
     :currencies/creation-date]}
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
