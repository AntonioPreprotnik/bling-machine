(ns pasta-xiana.views
  (:require
    [re-frame.core :as re-frame]
    [pasta-xiana.events :as events]
    [pasta-xiana.subs :as subs]))

(defn main-panel []
  (re-frame/dispatch [::events/fetch-todos!])
  (let [todos (re-frame/subscribe [::subs/todos])]
    [:div
     (map #(identity [:ul %])
       @todos)]))
