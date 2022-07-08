(ns app.frontend.ui.components.admin.create-user-form
  (:require
   [app.frontend.inputs :refer [wrapped-input]]
   [app.frontend.ui.components.button :refer [ButtonDefault]]
   [app.frontend.ui.components.password-field-secured :refer [PasswordFieldSecured]]
   [app.frontend.ui.components.shared-style :refer [error-msg-style
                                                    input-style]]
   [app.frontend.ui.components.switch-user-role :refer [SwitchUserRole]]
   [app.shared.util.inliner :as inliner :refer-macros [inline]]
   [helix.core :as hx :refer [$]]
   [helix.dom :as d]
   [keechma.next.controllers.malli-form.ui :as mfui]
   [keechma.next.helix.core :refer [dispatch use-sub with-keechma]]
   [keechma.next.helix.lib :refer [defnc]]))

(def add-user-input
  [{:attr :email :placeholder "Enter Email"}
   {:attr :first-name :placeholder "Enter First Name"}
   {:attr :last-name :placeholder "Enter Last Name"}])

(defnc AddUserForm [props]
  {:wrap [with-keechma]}
  (let [controller :create-user
        is-admin (use-sub props :switch-admin-role)
        on-switch #(dispatch props :switch-admin-role :toggle)
        password-value (mfui/use-get-in-data props controller :password)
        {:keys [submit-errors]} (use-sub props controller)]
    (d/div {:class "flex flex-col space-y-4"}
           ($ SwitchUserRole {:is-admin is-admin
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

                    add-user-input)
                   ($ PasswordFieldSecured
                     {:controller controller
                      :placeholder "Enter password"
                      :input-value password-value})
                   (d/div {:class "flex space-x-4"}
                          ($ ButtonDefault {:additional-style "mt-4"
                                            :label "Clear "
                                            :svg (inline "trash.svg")
                                            :type "reset"
                                            :on-click #(dispatch props controller :on-clear)})
                          ($ ButtonDefault {:additional-style "mt-4"
                                            :label "Add User"
                                            :svg (inline "add-user.svg")
                                            :type "submit"})))
           (when submit-errors
             (d/div {:class error-msg-style} submit-errors)))))
