(ns app.frontend.controllers.admin.current-admin
  (:require
   [com.verybigthings.funicular.controller :refer [query!]]
   [keechma.next.controller :as ctrl]
   [keechma.next.controllers.pipelines :as pipelines]
   [keechma.pipelines.core :as pp :refer-macros [pipeline!]]))

(derive :current-admin ::pipelines/controller)

(def get-current-admin
  (pipeline! [value {:keys [state*] :as ctrl}]
    (query! ctrl :api.user/get-current {:jwt value})
    (reset! state* value)))

(def pipelines
  {:keechma.on/start get-current-admin
   :refresh get-current-admin})

(defmethod ctrl/prep :current-admin [ctrl]
  (pipelines/register ctrl pipelines))
