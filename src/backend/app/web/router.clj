(ns app.web.router
  (:require [app.web.controllers.funicular :as funicular]
            [app.web.controllers.keechma :as keechma]
            [app.web.controllers.health :as health]
            [reitit.ring :as ring]))

(def routes
  [["/" {:action #'keechma/handle-index}]
   ["/health" {:action #'health/alive}]
   ["/assets/*" (ring/create-resource-handler {:path "/"})]
   ["/api" {:action #'funicular/handler}]])
