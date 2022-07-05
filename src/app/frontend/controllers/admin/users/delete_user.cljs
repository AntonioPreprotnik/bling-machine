(ns app.frontend.controllers.admin.users.delete-user
  (:require
   [com.verybigthings.funicular.controller :refer [command!]]
   [keechma.next.controller :as ctrl]
   [keechma.next.controllers.pipelines :as pipelines]
   [keechma.pipelines.core :as pp :refer-macros [pipeline!]]))

(derive :delete-user ::pipelines/controller)

(def pipelines
  {:on-delete-user  (pipeline! [value {:keys [deps-state*] :as ctrl}]
                      (command! ctrl :api.user/delete (-> @deps-state* :selected-user :selected-user-data :users/id))
                      (ctrl/dispatch ctrl :users :refresh)
                      (ctrl/dispatch ctrl :modal-delete-user :off))})

(defmethod ctrl/prep :delete-user [ctrl]
  (pipelines/register ctrl pipelines))
