(ns app.shared.schema.currencies)

(def registry
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

   :app.input.currency/fetch-by-date
   [:map
    :currencies/currency-name
    :currencies/creation-date]

   :app.input.currency/create
   [:map
    :currencies/currency-name
    :currencies/exchange-rate
    :currencies/creation-date]})
