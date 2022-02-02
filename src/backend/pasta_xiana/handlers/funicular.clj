(ns pasta-xiana.handlers.funicular
  (:require [pasta-xiana.funicular :as funicular]))

(defn handler [{:keys [funicular]}]
  {:post (fn [{:keys [body-params]}]
           (let [res (funicular/execute funicular body-params {})]
             {:status 200
              :body res}))})
