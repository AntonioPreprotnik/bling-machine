(ns app.backend.domain.handlers.currencies
  (:require
   [app.backend.penkala :as penkala]
   [app.shared.helpers :refer [select-as-simple-keys]]
   [clojure.string]
   [com.verybigthings.penkala.relation :as r]
   [jsonista.core :as j]))

(defn- insert-into-currencies [env data]
  (penkala/insert! env
                   (-> (r/->insertable (:currencies env))
                       (r/on-conflict-do-update
                        [:currency-name :creation-date]
                        {:exchange-rate :excluded/exchange-rate}))
                   data))

(defn- extract-and-map-keys [currency]
  {:currencies/currency-name (get currency "Valuta")
   :currencies/exchange-rate (-> (get currency "Srednji za devize")
                                 (clojure.string/replace "," ".")
                                 (read-string))
   :currencies/creation-date (get currency "Datum primjene")})

(defn get-and-parse-json [currency-name]
  (-> (str "https://api.hnb.hr/tecajn/v1?valuta=" currency-name)
      (slurp)
      (j/read-value)
      (first)
      (extract-and-map-keys)))

(defn get-and-parse-all-json [date-from date-to]
  (mapv (fn [currency] (-> (extract-and-map-keys currency)
                           (select-as-simple-keys [:currencies/currency-name
                                                   :currencies/exchange-rate
                                                   :currencies/creation-date])))
        (-> (str "https://api.hnb.hr/tecajn/v1?datum-od=" date-from "&datum-do=" date-to)
            (slurp)
            (j/read-value))))

(defn- insert-currency [{:keys [env]} data]
  (let [payload (select-as-simple-keys data [:currencies/currency-name
                                             :currencies/exchange-rate
                                             :currencies/creation-date])]
    (insert-into-currencies env payload)))

(defn get-currency [{{:keys [env]} :penkala
                     {id :currencies/id} :data}]
  (penkala/select-one! env (-> (:currencies env)
                               (r/where [:= :id [:cast id "uuid"]]))))

(defn get-currency-on-date [{{:keys [env]} :penkala
                             {name :currencies/currency-name
                              creation-date :currencies/creation-date} :data}]
  (penkala/select-one! env (-> (:currencies env)
                               (r/where  [:and [:= :currency-name [:cast name "text"]]
                                          [:= [:cast creation-date "text"] :creation-date]]))))

(defn create-currency [{:keys [penkala data]}]
  (penkala/with-transaction [penkala penkala]
    (let [currency (insert-currency penkala data)]
      {:currencies/id (:currencies/id currency)})))

(defn fetch-and-store-currency [{:keys [penkala data]}]
  (let [currency (get-and-parse-json (:currencies/currency-name data))]
    (insert-currency penkala currency)))

(defn get-unique-currencies [{{:keys [env]} :penkala}]
  (penkala/select! env (-> env
                           :currencies
                           (r/distinct)
                           (r/select [:currency-name])
                           (r/order-by [:currency-name]))))

(defn import-currencies [{{:keys [env]} :penkala}
                         {{:keys [date-from date-to]} :data}]
  (let [currencies (get-and-parse-all-json date-from date-to)]
    (insert-into-currencies env currencies)))

