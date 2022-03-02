(ns app.controllers.users
  (:require [keechma.next.controller :as ctrl]
            [keechma.next.controllers.pipelines :as pipelines]
            [keechma.next.controllers.entitydb :as edb]
            [keechma.pipelines.core :as pp :refer-macros [pipeline!]]
            [com.verybigthings.funicular.controller :refer [query!]]))

(derive :users ::pipelines/controller)

(def load-users
  (-> (pipeline! [value {:keys [deps-state*] :as ctrl}]
                 (query! ctrl :api.user/get-all {})
                 (edb/insert-collection! ctrl :entitydb :user ::list value))
      pp/use-existing
      pp/restartable))

(def pipelines
  {:keechma.on/start load-users

   :keechma.on/stop
   (pipeline! [_ ctrl]
              (edb/remove-collection! ctrl :entitydb ::list))})

(defmethod ctrl/prep :users [ctrl] (pipelines/register ctrl pipelines))

(defmethod ctrl/derive-state :users [_ _ {:keys [entitydb]}]
  (edb/get-collection entitydb ::list))