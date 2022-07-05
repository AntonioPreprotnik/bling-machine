(ns app.frontend.controllers.admin.users.edit-user
  (:require
   [app.shared.schema :as schema]
   [com.verybigthings.funicular.controller :refer [command!]]
   [keechma.malli-forms.core :as mf]
   [keechma.next.controller :as ctrl]
   [keechma.next.controllers.malli-form :as mfc]
   [keechma.next.controllers.pipelines :as pipelines]
   [keechma.pipelines.core :as pp :refer-macros [pipeline!]]))

(derive :edit-user ::pipelines/controller)

(def form (mf/make-form schema/registry :app.input.user/update {}))

(defn selected-user-data->submit-data-form [selected-user submit-data]
  (let [{:users/keys [id]} (:selected-user-data selected-user)]
    {:user-id id
     :data submit-data}))

(def initial-edit-user-form
  (pipeline! [value {:keys [meta-state* deps-state*] :as ctrl}]
    (let [selected-user (:selected-user @deps-state*)
          {:users/keys [id first-name last-name]} (:selected-user-data selected-user)]
      (pp/swap! meta-state* mfc/init-form form {:user-id (str id)
                                                :first-name first-name
                                                :last-name last-name}))))
(def edit-user
  (-> (pipeline! [value {:keys [meta-state* deps-state*] :as ctrl}]
        (command! ctrl :api.user/update (selected-user-data->submit-data-form
                                         (:selected-user @deps-state*)
                                         (merge
                                          {:is-admin (-> @deps-state* :selected-user :current-admin-role-status)}
                                          value)))
        (ctrl/dispatch ctrl :users :refresh)
        (ctrl/dispatch ctrl :modal-edit-user :off))
      mfc/wrap-submit))

(def pipelines
  (merge
   mfc/pipelines
   {:keechma.on/start initial-edit-user-form
    :on-submit edit-user}))

(defmethod ctrl/prep :edit-user [ctrl]
  (pipelines/register ctrl pipelines))
