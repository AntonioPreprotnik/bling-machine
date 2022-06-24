(ns app.frontend.controllers.generic.switch
  (:require
   [keechma.next.controller :as ctrl]))

(derive :generic/switch :keechma/controller)

(defmethod ctrl/start :generic/switch [ctrl _ _]
  (-> ctrl :generic.switch/default boolean))

(defmethod ctrl/handle :generic/switch [{:keys [state*]} ev _]
  (case ev
    :toggle (swap! state* not)
    :off (reset! state* false)
    :on (reset! state* true)
    nil))
