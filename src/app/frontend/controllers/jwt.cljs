(ns app.frontend.controllers.jwt
  (:require
   [app.settings :refer [jwt-name]]
   [com.verybigthings.funicular.controller :refer [command! get-command]]
   [hodgepodge.core :refer [get-item local-storage remove-item set-item]]
   [keechma.next.controller :as ctrl]
   [keechma.next.controllers.pipelines :as pipelines]
   [keechma.next.controllers.router :as router]
   [keechma.pipelines.core :as pp :refer-macros [pipeline!]]))

(derive :jwt ::pipelines/controller)

(defn set-jwt! [jwt]
  (set-item local-storage jwt-name jwt))

(def set-jwt
  (pipeline! [value {:keys [state*]}]
    (let [payload (get-command value)
          jwt (:jwt payload)]
      (set-jwt! jwt)
      (reset! state* jwt))))

(def clear-jwt
  (pipeline! [value {:keys [state*] :as ctrl}]
    (remove-item local-storage jwt-name)
    (router/redirect! ctrl :router {:page "home"})))

(def is-jwt-valid?
  (-> (pipeline! [value {:keys [state*] :as ctrl}]
        (let [jwt (get-item local-storage jwt-name)]
          (pipeline! [value {:keys [state*]}]
            (command! ctrl :api.session/check-jwt jwt)
            (if (= :valid value)
              (reset! state* jwt)
              clear-jwt))))
      (pp/set-queue :loading)))

(def pipelines
  {:keechma.on/start is-jwt-valid?
   :log-out clear-jwt
   [:funicular/after :api.session/login] set-jwt})

(defmethod ctrl/prep :jwt [ctrl]
  (pipelines/register ctrl pipelines))
