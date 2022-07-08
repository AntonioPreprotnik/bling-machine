(ns app.frontend.ui.components.admin.edit-user-form
  (:require
   [app.frontend.inputs :refer [wrapped-input]]
   [app.frontend.ui.components.button :refer [ButtonDefault]]
   [app.frontend.ui.components.shared-style :refer [error-msg-style
                                                    input-style]]
   [app.frontend.ui.components.switch-user-role :refer [SwitchUserRole]]
   [app.shared.util.inliner :as inliner :refer-macros [inline]]
   [helix.core :as hx :refer [$]]
   [helix.dom :as d]
   [helix.hooks :as hooks]
   [keechma.next.helix.core :refer [dispatch use-sub with-keechma]]
   [keechma.next.helix.lib :refer [defnc]]))

(def edit-user-input
  [{:attr :first-name :placeholder "Edit first name"}
   {:attr :last-name :placeholder "Edit last name"}
   {:attr :email :placeholder "Edit email"}])

(defnc EditUserForm [props]
  {:wrap [with-keechma]}
  (let [controller :edit-user
        {:users/keys [is-admin]} (:selected-user-data (use-sub props :selected-user))
        [current-admin-role-status set-current-admin-role-status] (hooks/use-state is-admin)
        on-switch #(do
                     (set-current-admin-role-status (not current-admin-role-status))
                     (dispatch props :selected-user :toggle-admin-state current-admin-role-status))]
    (d/div {:class "flex flex-col space-y-4"}
           ($ SwitchUserRole {:is-admin current-admin-role-status
                              :on-switch on-switch})
           (d/form {:class "w-full flex flex-col space-y-4"
                    :onSubmit (fn [e]
                                (.preventDefault e)
                                (dispatch props controller :on-submit))}
                   (map-indexed
                    (fn [idx {:keys [attr placeholder]}]
                      (d/div {:key idx}
                             (wrapped-input {:keechma.form/controller controller
                                             :input/style input-style
                                             :input/error-msg-style error-msg-style
                                             :input/type :text
                                             :input/attr attr
                                             :placeholder placeholder})))
                    edit-user-input)
                   (d/div {:class "flex space-x-4"}
                          ($ ButtonDefault {:additional-style "mt-4"
                                            :label "Clear "
                                            :svg (inline "trash.svg")
                                            :type "reset"
                                            :on-click #(dispatch props controller :on-clear)})
                          ($ ButtonDefault {:additional-style "mt-4"
                                            :label "Edit User"
                                            :svg (inline "edit-pencil.svg")
                                            :type "submit"}))))))
