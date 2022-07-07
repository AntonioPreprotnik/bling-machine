(ns app.frontend.ui.components.admin.create-user-form
  (:require
   [app.frontend.inputs :refer [wrapped-input]]
   [app.frontend.ui.components.button :refer [ButtonDefaul]]
   [app.frontend.ui.components.switch-user-role :refer [SwitchUserRole]]
   [app.frontend.ui.pages.home :refer [error-msg-style input-style]]
   [app.shared.util.inliner :as inliner :refer-macros [inline]]
   [helix.core :as hx :refer [$]]
   [helix.dom :as d]
   [keechma.next.helix.core :refer [dispatch use-sub with-keechma]]
   [keechma.next.helix.lib :refer [defnc]]))

(def add-user-input
  [{:attr :email :placeholder "Enter Email"}
   {:attr :first-name :placeholder "Enter First Name"}
   {:attr :last-name :placeholder "Enter Last Name"}
   {:attr :password :placeholder "Enter Password"}])

(defnc AddUserForm [props]
  {:wrap [with-keechma]}
  (let [controller :create-user
        is-admin (use-sub props :switch-admin-role)
        on-switch #(dispatch props :switch-admin-role :toggle)]
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
                   ($ ButtonDefaul {:additional-style "w-full flex justify-center mt-4"
                                    :label "Add User"
                                    :svg (inline "add-user.svg")})))))
