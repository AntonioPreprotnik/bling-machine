(ns app.frontend.ui.pages.currencies
  (:require
   [app.frontend.inputs :refer [Dropdown]]
   [cljc.java-time.local-date :refer [now]]
   [clojure.string :refer [split]]
   [helix.core :as hx :refer [$]]
   [helix.dom :as d]
   [keechma.next.helix.core :refer [dispatch use-sub with-keechma]]
   [keechma.next.helix.lib :refer [defnc]]))

(defn- parse [date]
  (let [[year month day] (split date #"-")]
    (str day "." month "." year)))

(defnc Currencies [props]
  {:wrap [with-keechma]}
  (let [unique-currencies (:unique-currencies (use-sub props :currencies))
        current-value (:current-value (use-sub props :currencies))
        current-date (:current-date (use-sub props :currencies))
        from-currency (:selected-from-currency (use-sub props :currencies))
        from-currency-name (:currencies/currency-name from-currency)
        from-currency-exchange-rate (:currencies/exchange-rate from-currency)
        to-currency (:selected-to-currency (use-sub props :currencies))
        to-currency-name (:currencies/currency-name to-currency)
        to-currency-exchange-rate (:currencies/exchange-rate to-currency)
        conversion-rate (if (and (not= from-currency-exchange-rate nil)
                                 (not= to-currency-exchange-rate nil))
                          (/ to-currency-exchange-rate from-currency-exchange-rate) nil)]
    (d/div {:class "flex flex-col w-screen p-4 space-y-4 bg-gray-100 justify-center items-center h-screen"}
           (d/div {:class "flex p-4 w-full justify-center space-x-4"}
                  (d/div {:class "flex w-1/2"}
                         (d/div {:class "flex flex-col w-1/2"}
                                (d/div  "FROM")
                                ($ Dropdown {:selected from-currency-name
                                             :options (mapv (fn [currency] {:value (:currencies/currency-name currency)
                                                                            :label (:currencies/currency-name currency)})
                                                            unique-currencies)
                                             :on-change #(dispatch props
                                                                   :currencies
                                                                   :fetch-from-currency
                                                                   {:currency-name %
                                                                    :current-value current-value
                                                                    :current-date current-date})}))
                         (d/div {:class "flex flex-col w-1/2"}
                                (d/div {:class "flex"} "AMOUNT")
                                (d/div {:class "flex flex-row h-full"}
                                       (d/input {:type "number"
                                                 :on-change #(dispatch props
                                                                       :currencies
                                                                       :change-amount
                                                                       {:current-value (.. % -target -value)})}))))

                  (d/div {:class "flex w-1/2"}
                         (d/div {:class "flex flex-col w-1/2"}
                                (d/div  "TO")
                                ($ Dropdown {:selected to-currency-name
                                             :options (mapv (fn [currency] {:value (:currencies/currency-name currency)
                                                                            :label (:currencies/currency-name currency)})
                                                            unique-currencies)
                                             :on-change #(dispatch props
                                                                   :currencies
                                                                   :fetch-to-currency
                                                                   {:currency-name %
                                                                    :current-value current-value
                                                                    :current-date current-date})}))
                         (d/div {:class "flex flex-col w-1/2"}
                                (d/div {:class "flex"} "RESULT")
                                (d/div {:class "flex flex-row h-full bg-white items-center"} (when (not= conversion-rate nil) (* conversion-rate current-value))))))

           (d/div {:class "flex flex-col p-4 w-full justify-center items-center space-y-4"}
                  (d/div {:class "flex flex-col items-center space-y-2"}
                         (d/div "EXCHANGE RATE ON DATE")
                         (d/div {:class "flex bg-cyan-400 items-center rounded-full"}
                                (if (and (not= to-currency nil) (not= from-currency nil))
                                  (str "1" from-currency-name "=" conversion-rate to-currency-name)
                                  "Currencies not selected !")))
                  (when (and (some? from-currency)
                             (some? to-currency)
                             (some? current-value))
                    (d/div
                     {:class "flex items-center "}
                     (d/input {:type "date"
                               :min "2020-01-01"
                               :max (str (now))
                               :on-change #(dispatch props
                                                     :currencies
                                                     :change-date
                                                     {:date (parse (str (.. % -target -value)))
                                                      :from-currency-name from-currency-name
                                                      :to-currency-name to-currency-name})})))))))




