(ns app.backend.domain.handlers.currencies
  (:require
   [app.backend.penkala :as penkala]
   [app.shared.helpers :refer [select-as-simple-keys]]
   [com.verybigthings.penkala.relation :as r]))

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

