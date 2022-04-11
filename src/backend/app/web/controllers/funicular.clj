(ns app.web.controllers.funicular
  (:require [app.funicular :as api]))

(defn handler [{:keys [deps request] :as state}]
  (let [{:app/keys [funicular]} deps
        {:keys [body-params]}   request
        res                     (api/execute funicular body-params {})]
    (-> state
        (assoc-in [:response :status] 200)
        (assoc-in [:response :body] res))))



