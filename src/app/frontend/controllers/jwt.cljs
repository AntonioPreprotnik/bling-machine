(ns app.frontend.controllers.jwt
  (:require
   [app.settings :refer [jwt-name]]
   [clojure.core.match :refer-macros [match]]
   [com.verybigthings.funicular.controller :refer [command! get-command]]
   [hodgepodge.core :refer [get-item local-storage remove-item set-item]]
   [keechma.next.controller :as ctrl]
   [keechma.next.controllers.pipelines :as pipelines]
   [keechma.next.controllers.router :as router]
   [keechma.pipelines.core :as pp :refer-macros [pipeline!]]
   [promesa.core :as p]))

(derive :jwt ::pipelines/controller)

(defn set-jwt! [jwt]
  (set-item local-storage jwt-name jwt))

(defn get-ls-jwt []
  (get-item local-storage jwt-name))

(def set-jwt
  (pipeline! [value {:keys [state*] :as ctrl}]
    (let [payload (get-command value)
          jwt (:jwt payload)]
      (set-jwt! jwt)
      (reset! state* jwt)
      (ctrl/dispatch ctrl :url :refresh))))

(def clear-jwt
  (pipeline! [value {:keys [state*] :as ctrl}]
    (remove-item local-storage jwt-name)
    (reset! state* nil)
    (router/redirect! ctrl :router {:page "home"})))

(def is-jwt-valid?
  (-> (pipeline! [value {:keys [state*] :as ctrl}]
        (let [jwt (get-ls-jwt)]
          (pipeline! [value {:keys [state*]}]
            (command! ctrl :api.session/check-jwt jwt)
            (if (= :valid value)
              (reset! state* jwt)
              clear-jwt))))
      (pp/set-queue :loading)))

(def is-jwt-expired?
  (-> (pipeline! [value {:keys [state*] :as ctrl}]
        (let [jwt (get-ls-jwt)]
          (pipeline! [value {:keys [state*]}]
            (command! ctrl :api.session/check-jwt jwt)
            (println "is-jwt-expired?" value)
            (match [(boolean (seq jwt)) value]
              [false :expired]  clear-jwt
              [true :expired]  clear-jwt
              [true :valid] (pipeline! [_value ctrl]
                              (reset! state* jwt)
                              (p/delay (* 3000 10))
                              #_{:clj-kondo/ignore [:unresolved-var]}
                              (ctrl/dispatch-self ctrl :re-check-jwt))))))
      (pp/set-queue :loading)))

(def pipelines
  {:keechma.on/start is-jwt-valid?
   :log-out clear-jwt
   :re-check-jwt is-jwt-expired?
   [:funicular/after :api.session/login] set-jwt})

(defmethod ctrl/prep :jwt [ctrl]
  (pipelines/register ctrl pipelines))
