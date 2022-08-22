(ns app.backend.domain.handlers.currencies
  (:require
   [app.backend.penkala :as penkala]
   [app.shared.helpers :refer [select-as-simple-keys]]
   [clojure.string]
   [com.verybigthings.penkala.relation :as r]
   [jsonista.core :as j]))

(defn- extract-and-map-keys [currency]
  (if (= currency nil)
    {:error "Currency invalid!"}
    {:currencies/currency-name (get currency "Valuta")
     :currencies/exchange-rate (-> (get currency "Prodajni za devize")
                                   (clojure.string/replace "," ".")
                                   (read-string))
     :currencies/creation-date (get currency "Datum primjene")}))

(defn get-and-parse-json [currency-name]
  (-> (str "https://api.hnb.hr/tecajn/v1?valuta=" currency-name)
      (slurp)
      (j/read-value)
      (first)
      (extract-and-map-keys)))

(defn- insert-currency [{:keys [env]} data]
  (let [payload (select-as-simple-keys data [:currencies/currency-name :currencies/exchange-rate :currencies/creation-date])]
    (penkala/insert! env
                     (-> (r/->insertable (:currencies env))
                         (r/on-conflict-do-update
                          [:currency-name :creation-date]
                          {:exchange-rate (:exchange-rate payload)}))
                     payload)))

(defn get-currency [{{:keys [env]} :penkala
                     {id :currencies/id} :data}]
  (penkala/select-one! env (-> (:currencies env)
                               (r/where [:= :id [:cast id "uuid"]]))))

(defn create-currency [{:keys [penkala data]}]
  (penkala/with-transaction [penkala penkala]
    (let [currency (insert-currency penkala data)]
      {:currencies/id (:currencies/id currency)})))

(defn fetch-and-store-curreny [{:keys [penkala data]}]
  (let [currency (get-and-parse-json (:currencies/currency-name data))]
    (if (= currency {:error "Currency invalid!"})
      currency
      (insert-currency penkala currency))))

(defn get-unique-currencies [{{:keys [env]} :penkala}]
  (penkala/select! env (-> env
                           :currencies
                           (r/distinct)
                           (r/select [:currency-name])
                           (r/order-by [:currency-name]))))

