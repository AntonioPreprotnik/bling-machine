(ns app.frontend.ui.components.admin.create-user-form
  (:require
   [app.frontend.inputs :refer [wrapped-input]]
   [app.frontend.ui.components.button :refer [ButtonDefaul]]
   [app.frontend.ui.pages.home :refer [error-msg-style input-style]]
   [app.shared.util.inliner :as inliner :refer-macros [inline]]
   [helix.core :as hx :refer [$]]
   [helix.dom :as d]
   [keechma.next.helix.classified :refer [defclassified]]
   [keechma.next.helix.core :refer [dispatch use-sub with-keechma]]
   [keechma.next.helix.lib :refer [defnc]]))

(defclassified SwitchUserText :div "text-lg text-gray-900")
(defclassified SwitchCheckboxWrap :button "relative inline-flex h-[38px] w-[74px] shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none focus-visible:ring-2 focus-visible:ring-white focus-visible:ring-opacity-75 ")
(defclassified SwitchCheckboxCircle :span "pointer-events-none inline-block h-[34px] w-[34px] transform rounded-full bg-white shadow-lg ring-0 transition duration-200 ease-in-out")

(def add-user-input
  [{:attr :email :placeholder "Enter Email"}
   {:attr :first-name :placeholder "Enter First Name"}
   {:attr :last-name :placeholder "Enter Last Name"}
   {:attr :password-hash :placeholder "Enter Password"}])

(defnc SwitchUserRole [{:keys [is-admin on-switch]}]
  (d/div {:class "flex justify-center items-center space-x-6"}
         ($ SwitchUserText
           (str "Set " (if is-admin "off" "on") " admin role"))
         ($ SwitchCheckboxWrap
           {:onClick on-switch
            :class [(if is-admin "bg-green-400" "bg-red-400")]}
           ($ SwitchCheckboxCircle
             {:aria-hidden true
              :class [(if is-admin "translate-x-9" "translate-x-0")]}))))

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
