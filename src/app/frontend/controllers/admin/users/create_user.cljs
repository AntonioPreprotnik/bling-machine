(ns app.frontend.controllers.admin.users.create-user
  (:require
   [app.shared.schema :as schema]
   [com.verybigthings.funicular.controller :refer [command!]]
   [keechma.malli-forms.core :as mf]
   [keechma.next.controller :as ctrl]
   [keechma.next.controllers.malli-form :as mfc]
   [keechma.next.controllers.pipelines :as pipelines]
   [keechma.pipelines.core :as pp :refer-macros [pipeline!]]))

(derive :create-user ::pipelines/controller)

(def form (mf/make-form schema/registry :app.input.user/create {}))

(def initial-create-user-form
  (pipeline! [value {:keys [meta-state*]}]
    (pp/swap! meta-state* mfc/init-form form)))

(def create-user
  (-> (pipeline! [value {:keys [meta-state* deps-state*] :as ctrl}]
        (command! ctrl :api.user/create (merge {:is-admin (:switch-admin-role @deps-state*)} value))
        (ctrl/dispatch ctrl :users :refresh)
        (ctrl/dispatch ctrl :modal-add-user :off))
      mfc/wrap-submit))

(def pipelines
  (merge
   mfc/pipelines
   {:keechma.on/start initial-create-user-form
    :on-clear initial-create-user-form
    :on-submit create-user}))

(defmethod ctrl/prep :create-user [ctrl]
  (pipelines/register ctrl pipelines))
