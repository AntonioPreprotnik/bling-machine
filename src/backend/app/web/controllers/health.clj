(ns app.web.controllers.health
  (:require [ring.util.response :as ring]
            [xiana.core :as xiana]))

(defn alive [state]
  (-> state
      (assoc :response (ring/response "Alive"))
      (xiana/ok)))
