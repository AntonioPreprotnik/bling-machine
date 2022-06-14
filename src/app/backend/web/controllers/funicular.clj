(ns backend.web.controllers.funicular
  (:require
   [backend.funicular :as funicular]))

(defn handler [{:keys [deps request] :as state}]
  (let [{:app/keys [funicular]} deps
        {:keys [body-params]}   request
        res                     (funicular/execute funicular body-params {})]
    (-> state
        (assoc-in [:response :status] 200)
        (assoc-in [:response :body] res))))



