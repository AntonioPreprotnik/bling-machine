(ns app.controllers.current-user
  (:require [keechma.next.controller :as ctrl]
            [keechma.next.controllers.pipelines :as pipelines]
            [keechma.next.controllers.entitydb :as edb]
            [keechma.pipelines.core :as pp :refer-macros [pipeline!]]
            [com.verybigthings.funicular.controller :refer [query!]]))

(derive :current-user ::pipelines/controller)

(def pipelines
  {:keechma.on/start (-> (pipeline! [value {:keys [deps-state*] :as ctrl}]
                                    (query! ctrl :api.user/get-one {:user-id (-> ctrl :keechma.controller/params uuid)})
                                    (edb/insert-named! ctrl :entitydb :user ::current value))
                         (pp/set-queue :loading))

   :keechma.on/stop
   (pipeline! [_ ctrl] (edb/remove-named! ctrl :entitydb ::current))})

(defmethod ctrl/prep :current-user [ctrl] (pipelines/register ctrl pipelines))

(defmethod ctrl/derive-state :current-user [_ _ {:keys [entitydb]}]
  (edb/get-named entitydb ::current))