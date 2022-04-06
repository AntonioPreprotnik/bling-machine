(ns app.controllers.current-user
  (:require [com.verybigthings.funicular.controller :refer [query!]]
            [keechma.next.controller :as ctrl]
            [keechma.next.controllers.entitydb :as edb]
            [keechma.next.controllers.pipelines :as pipelines]
            [keechma.pipelines.core :as pp :refer-macros [pipeline!]]))

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
