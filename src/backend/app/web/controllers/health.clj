(ns app.web.controllers.health
  (:require [xiana.core :as xiana]
            [ring.util.response :as ring]))

(defn alive [state]
  (-> state
      (assoc :response (ring/response "Alive"))
      (xiana/ok)))
