(ns app.controllers.current-user
  (:require [keechma.next.controller :as ctrl]
            [keechma.next.controllers.pipelines :as pipelines]
            [keechma.next.controllers.entitydb :as edb]
            [keechma.pipelines.core :as pp :refer-macros [pipeline!]]
            [com.verybigthings.funicular.controller :refer [req! query!]]))

(derive :current-user ::pipelines/controller)

(def pipelines
  {:keechma.on/start (-> (pipeline! [value {:keys [deps-state*], :as ctrl}]
                           (println "here")
                           (query! ctrl :api.user/get-one {:user-id "286696ac-5977-4b3e-8392-5ec4a8e3784e"})
                           (edb/insert-named! ctrl :entitydb :user :user/current value))
                         (pp/set-queue :loading))

   :keechma.on/stop
   (pipeline! [_ ctrl] (edb/remove-named! ctrl :entitydb :user/current))})

(defmethod ctrl/prep :current-user [ctrl] (pipelines/register ctrl pipelines))

(defmethod ctrl/derive-state :current-user
  [_ _ {:keys [entitydb]}]
  (edb/get-named entitydb :user/current))