(ns app.frontend.controllers.jwt
  (:require
   [app.settings :refer [jwt-name]]
   [hodgepodge.core :refer [get-item local-storage remove-item]]
   [keechma.next.controller :as ctrl]
   [keechma.next.controllers.pipelines :as pipelines]
   [keechma.next.controllers.router :as router]
   [keechma.pipelines.core :as pp :refer-macros [pipeline!]]))

(derive :jwt ::pipelines/controller)

(def clear-jwt
  (pipeline! [value {:keys [state*] :as ctrl}]
    (remove-item local-storage jwt-name)
    (router/redirect! ctrl :router {:page "home"})))

(def pipelines
  {:keechma.on/start (-> (pipeline! [value {:keys [state*] :as ctrl}]
                           (let [jwt (get-item local-storage jwt-name)]
                             (reset! state* jwt)))
                         (pp/set-queue :loading))
   :log-out clear-jwt})

(defmethod ctrl/prep :jwt [ctrl]
  (pipelines/register ctrl pipelines))
