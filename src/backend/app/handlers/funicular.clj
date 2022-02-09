(ns app.handlers.funicular
  (:require [app.funicular :as api]
            [tdebug :refer [trace> trace>>]]
            [xiana.core :as xiana]))

(defn handler [{:keys [deps request] :as state}]
  (let [{:app/keys [funicular]} deps
        {:keys [body-params]} request
        res (api/execute funicular body-params {})]
    (xiana/ok (-> state
                  (assoc-in [:response :status] 200)
                  (assoc-in [:response :body] res)))))



