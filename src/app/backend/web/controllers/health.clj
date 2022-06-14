(ns backend.web.controllers.health
  (:require
   [ring.util.response :as ring]))

(defn alive [state]
  (assoc state :response (ring/response "Alive")))
