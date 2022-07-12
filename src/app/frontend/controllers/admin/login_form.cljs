(ns app.frontend.controllers.admin.login-form
  (:require
   [app.settings :refer [jwt-name]]
   [app.shared.schema :as schema]
   [com.verybigthings.funicular.controller :refer [command!]]
   [hodgepodge.core :refer [local-storage set-item]]
   [keechma.malli-forms.core :as mf]
   [keechma.next.controller :as ctrl]
   [keechma.next.controllers.malli-form :as mfc]
   [keechma.next.controllers.pipelines :as pipelines]
   [keechma.next.controllers.router :as router]
   [keechma.pipelines.core :as pp :refer-macros [pipeline!]]))

(derive :login-form ::pipelines/controller)

(def form (mf/make-form schema/registry :app.input.login nil))

(defn set-jwt! [jwt]
  (set-item local-storage jwt-name jwt))

(def initial-login-form
  (pipeline! [value {:keys [meta-state* schema]}]
    (pp/swap! meta-state* mfc/init-form form)))

(def submit-admin-credentials
  (-> (pipeline! [value {:keys [state*] :as ctrl}]
        (command! ctrl :api.session/login value)
        (when-let [jwt (:jwt value)]
          (set-jwt! jwt))
        (router/redirect! ctrl :router {:page "admin"})
        (rescue! [error]
          (pp/swap! state* assoc :submit-errors (ex-message error))))
      mfc/wrap-submit))

(def clear-submit-errors
  (pipeline! [value {:keys [state*]}]
    mfc/on-partial-change
    (pp/swap! state* dissoc :submit-errors)))

(def pipelines
  (merge
   mfc/pipelines
   {:keechma.on/start initial-login-form
    :keechma.form/on-partial-change clear-submit-errors
    :on-submit submit-admin-credentials}))

(defmethod ctrl/prep :login-form [ctrl]
  (pipelines/register ctrl pipelines))
