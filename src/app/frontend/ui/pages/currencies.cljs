(ns app.frontend.ui.pages.currencies
  (:require
   [app.frontend.inputs :refer [Dropdown]]
   [helix.core :as hx :refer [$]]
   [helix.dom :as d]
   [keechma.next.helix.core :refer [dispatch use-sub with-keechma]]
   [keechma.next.helix.lib :refer [defnc]]))

(defnc Currencies [props]
  {:wrap [with-keechma]}
  (let [unique-currencies (:unique-currencies (use-sub props :currencies))
        current-value (:current-value (use-sub props :currencies))
        from-currency (:selected-from-currency (use-sub props :currencies))
        from-currency-name (:currencies/currency-name from-currency)
        from-currency-exchange-rate (:currencies/exchange-rate from-currency)
        to-currency (:selected-to-currency (use-sub props :currencies))
        to-currency-name (:currencies/currency-name to-currency)
        to-currency-exchange-rate (:currencies/exchange-rate to-currency)
        conversion-rate (if (and (not= from-currency-exchange-rate nil) (not= to-currency-exchange-rate nil)) (/ to-currency-exchange-rate from-currency-exchange-rate) nil)]
    (d/div {:class "flex flex-col w-screen p-4 space-y-4 bg-gray-100 justify-center items-center h-screen"}
           (d/div {:class "w-80 h-20"}
                  (d/div  "FROM")
                  (d/div {:class "flex flex-row w-80"}
                         ($ Dropdown {:selected from-currency-name
                                      :options (mapv (fn [currency] {:value (:currencies/currency-name currency)
                                                                     :label (:currencies/currency-name currency)})
                                                     unique-currencies)
                                      :on-change #(dispatch props
                                                            :currencies
                                                            :fetch-from-currency
                                                            {:currency-name %
                                                             :current-value current-value})})
                         (d/input {:type "number"
                                   :on-change #(dispatch props
                                                         :currencies
                                                         :change-amount
                                                         {:current-value (.. % -target -value)})})))

           (d/div {:class "flex items-center h-12 w-80 "}
                  (d/div {:class "flex basis-1/2 bg-cyan-400 rounded-full h-12 text-center items-center"}
                         (if (and (not= to-currency nil) (not= from-currency nil))
                           (str "1" from-currency-name "=" conversion-rate to-currency-name)
                           "Currencies not selected !")))

           (d/div {:class "w-80"}
                  (d/div "TO")
                  (d/div {:class "flex flex-row  items-center"}
                         (d/div
                          ($ Dropdown {:selected to-currency-name
                                       :options (mapv (fn [currency] {:value (:currencies/currency-name currency)
                                                                      :label (:currencies/currency-name currency)})
                                                      unique-currencies)
                                       :on-change #(dispatch props
                                                             :currencies
                                                             :fetch-to-currency
                                                             {:currency-name %
                                                              :current-value current-value})}))
                         (d/div {:class "w-3/4 bg-white  h-14"} (when (not= conversion-rate nil) (* conversion-rate current-value))))))))




