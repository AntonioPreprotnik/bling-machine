(ns app.frontend.controllers.admin.users.selected-user
  (:require
   [keechma.next.controller :as ctrl]
   [keechma.next.controllers.pipelines :as pipelines]
   [keechma.pipelines.core :as pp :refer-macros [pipeline!]]))

(derive :selected-user ::pipelines/controller)

(def pipelines
  {:on-select-user (pipeline! [value {:keys [state*] :as ctrl}]
                     (pp/swap! state* assoc :selected-user-data value))

   :toggle-admin-state (pipeline! [value {:keys [state*] :as ctrl}]
                         (pp/swap! state* assoc :current-admin-role-status (not value)))})

(defmethod ctrl/prep :selected-user [ctrl]
  (pipelines/register ctrl pipelines))
