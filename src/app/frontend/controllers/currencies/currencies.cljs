(ns app.frontend.controllers.currencies.currencies
  (:require
   [app.shared.schema :as schema]
   [com.verybigthings.funicular.controller :refer [command! query!]]
   [keechma.malli-forms.core :as mf]
   [keechma.next.controller :as ctrl]
   [keechma.next.controllers.entitydb :as edb]
   [keechma.next.controllers.pipelines :as pipelines]
   [keechma.pipelines.core :as pp :refer-macros [pipeline!]]))

(derive :currencies ::pipelines/controller)

(def form (mf/make-form schema/registry :app/currencies {}))

(def load-currencies
  (-> (pipeline! [value {:keys [deps-state* meta-state* state*] :as ctrl}]
        (query! ctrl :api.currencies/get-unique-currencies {})
        (pp/swap! state* assoc :unique-currencies value))
      pp/use-existing
      pp/restartable))

(def pipelines
  {:keechma.on/start load-currencies
   :refresh load-currencies

   :fetch-from-currency
   (pipeline! [value ctrl]
     (let [{:keys [currency-name current-value]} value]
       (pipeline! [value {:keys [meta-state* state*] :as ctrl}]
         (pp/swap! state* assoc :current-value current-value)
         (command! ctrl :api.currencies/fetch-and-store-curreny {:currencies/currency-name currency-name})
         (pp/swap! state* assoc :selected-from-currency value))))

   :fetch-to-currency
   (pipeline! [value ctrl]
     (let [{:keys [currency-name current-value]} value]
       (pipeline! [value {:keys [meta-state* state*] :as ctrl}]
         (pp/swap! state* assoc :current-value current-value)
         (command! ctrl :api.currencies/fetch-and-store-curreny {:currencies/currency-name currency-name})
         (pp/swap! state* assoc :selected-to-currency value))))

   :change-amount
   (pipeline! [value ctrl]
     (let [{:keys [current-value]} value]
       (pipeline! [value {:keys [meta-state* state*] :as ctrl}]
         (pp/swap! state* assoc :current-value current-value))))

   :keechma.on/stop
   (pipeline! [_ ctrl]
     (edb/remove-collection! ctrl :entitydb ::list))})

(defmethod ctrl/prep :currencies [ctrl] (pipelines/register ctrl pipelines))

