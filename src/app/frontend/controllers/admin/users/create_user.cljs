(ns app.frontend.controllers.admin.users.create-user
  (:require
   [com.verybigthings.funicular.controller :refer [command!]]
   [keechma.malli-forms.core :as mf]
   [keechma.next.controller :as ctrl]
   [keechma.next.controllers.malli-form :as mfc]
   [keechma.next.controllers.pipelines :as pipelines]
   [keechma.pipelines.core :as pp :refer-macros [pipeline!]]
   [schema :as schema]))

(derive :create-user ::pipelines/controller)

(def form (mf/make-form schema/registry :app.input.user/create {}))

(def initial-create-user-form
  (pipeline! [value {:keys [meta-state*]}]
    (pp/swap! meta-state* mfc/init-form form)))

(def create-user
  (-> (pipeline! [value {:keys [meta-state* on-submit] :as ctrl}]
        (command! ctrl :api.user/create value)
        (ctrl/dispatch ctrl :modal-add-user :off))
      mfc/wrap-submit))

(def pipelines
  (merge
   mfc/pipelines
   {:keechma.on/start initial-create-user-form
    :on-submit create-user}))

(defmethod ctrl/prep :create-user [ctrl]
  (pipelines/register ctrl pipelines))
