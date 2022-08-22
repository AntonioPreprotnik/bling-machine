(ns app.backend.domain.handlers.currency-test
  (:require
   [app.backend.helpers :refer [command! query!]]
   [app.test-core :refer [get-system]]
   [app.test-fixtures :refer [clean-db]]
   [clojure.test :refer [use-fixtures]]
   [state-flow.api :as flow :refer [flow]]
   [state-flow.assertions.matcher-combinators :refer [match?]]
   [state-flow.cljtest :refer [defflow]]))

(use-fixtures :each clean-db)

(defn init []
  (let [system (get-system)]
    {:system system}))

(defn create-currency [currency_name exchange_rate creation_date]
  (flow "Create currency"
    (flow/swap-state
     (fn [{:keys [_ _] {funicular :app/funicular} :system :as state}]
       (let [new-currency (command! funicular :api.currencies/create-currency {:currencies/currency-name currency_name :currencies/exchange-rate exchange_rate :currencies/creation-date creation_date})]
         (assoc state :new-currency new-currency))))
    (flow/get-state :new-currency)))

(defn get-currency [id]
  (flow "GET currency"
    (flow/swap-state
     (fn [{:keys [_ _] {funicular :app/funicular} :system :as state}]
       (let [currency (query! funicular :api.currencies/get-currency :currency {:currencies/id id})]
         (assoc state :currency currency))))
    (flow/get-state :currency)))

(defn get-unique-currencies []
  (flow "GET unique currencies"
    (flow/swap-state
     (fn [{:keys [_ _] {funicular :app/funicular} :system :as state}]
       (let [currencies (query! funicular :api.currencies/get-unique-currencies :currencies {})]
         (assoc state :currencies currencies))))
    (flow/get-state :currencies)))

(defn fetch-and-store-curreny [currency_name]
  (flow "Fetch and store currency"
    (flow/swap-state
     (fn [{:keys [_ _] {funicular :app/funicular} :system :as state}]
       (let [fetched-currency (command! funicular :api.currencies/fetch-and-store-curreny {:currencies/currency-name currency_name})]
         (assoc state :fetched-currency fetched-currency))))
    (flow/get-state :fetched-currency)))

(defflow create-and-get-currencies-test
  {:init init}
  [currency-id (create-currency "GBP" 7.863463463 "12.03.2021")]
  [currency (get-currency (:currencies/id currency-id))]
  (match? {:currencies/exchange-rate 7.863463463,
           :currencies/currency-name "GBP",
           :currencies/creation-date "12.03.2021"}
          currency))

(defflow create-currency-with-same-name-and-date-updates-exchange-rate-test
  {:init init}
  (create-currency "EUR" 5 "12.03.2021")
  [currency-id (create-currency "EUR" 7.92 "12.03.2021")]
  [currency (get-currency (:currencies/id currency-id))]
  (match? {:currencies/exchange-rate 7.92,
           :currencies/currency-name "EUR",
           :currencies/creation-date "12.03.2021"}
          currency))

(defflow fetch-and-store-single-currency
  {:init init}
  [currency (fetch-and-store-curreny "EUR")]
  [currency-db (get-currency (:currencies/id currency))]
  (match? currency-db currency))

(defflow currency-dose-not-exist
  {:init init}
  [currency (fetch-and-store-curreny "BAD")]
  (match? {:error "Currency invalid!"} currency))

(defflow get-unique-currencies-returns-unique-currencies
  {:init init}
  (create-currency "EUR" 7.85 "12.03.2021")
  (create-currency "EUR" 7.88 "15.03.2021")
  (create-currency "GBP" 8.25 "12.03.2021")
  [currencies (get-unique-currencies)]
  (match? [#:currencies{:currency-name "EUR"}
           #:currencies{:currency-name "GBP"}]
          currencies))
