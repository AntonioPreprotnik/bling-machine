(ns app.frontend.ui.pages.home
  (:require
   [app.frontend.inputs :refer [wrapped-input]]
   [app.frontend.ui.components.button :refer [ButtonDefaul]]
   [app.shared.util.inliner :as inliner :refer-macros [inline]]
   [helix.core :as hx :refer [$]]
   [helix.dom :as d]
   [helix.hooks :as hooks]
   [keechma.next.controllers.malli-form.ui :as mfui]
   [keechma.next.helix.classified :refer [defclassified]]
   [keechma.next.helix.core :refer [dispatch use-sub with-keechma]]
   [keechma.next.helix.lib :refer [defnc]]))

(defclassified HomeWrap :div "h-screen w-screen flex flex-col items-center justify-around font-light")
(defclassified FormWrap :div "p-10 border border-gray-400 rounded-lg flex flex-col items-center justify-center space-y-6")
(defclassified FormContainer :form "flex flex-col items-center space-y-6 w-96")
(defclassified SignUpBtn :button "inline hover:text-gray-400 underline underline-offset-2")
(defclassified LogInBtn :button "flex py-2 px-3 border border-gray-400 rounded-lg hover:bg-gray-400 hover:text-white disabled:bg-gray-400 disabled:opacity-50 disabled:text-black")
(defclassified PasswordFieldWrap :div "w-full relative")

(def input-style "w-full h-12 border border-gray-400 px-10 py-2 rounded-lg focus:outline-none")
(def error-msg-style "text-red-400 text-sm")
(def title-style "text-lg font-semibold")

(defnc PasswordField [_]
  (let [[password-mask set-password-mask] (hooks/use-state true)]
    ($ PasswordFieldWrap
      (wrapped-input {:keechma.form/controller :login-form
                      :input/style input-style
                      :input/error-msg-style error-msg-style
                      :input/type (if password-mask :password :text)
                      :input/attr :password
                      :placeholder "Enter password"})
      (d/button {:type "button"
                 :onClick #(set-password-mask not)
                 :style {:position "absolute"
                         :right 10
                         :top 12}}
                (if password-mask
                  (inline "security-eye-slash.svg")
                  (inline "security-eye.svg"))))))

(defnc InputGroupLogin [_]
  (d/div {:class "flex flex-col space-y-4 w-full"}
         (wrapped-input {:keechma.form/controller :login-form
                         :input/style input-style
                         :input/error-msg-style error-msg-style
                         :input/type  :text
                         :input/attr :email
                         :placeholder "Enter email address"})
         ($ PasswordField)))

(defnc SignUpForm [{:keys [set-is-sign-up is-sign-up]}]
  ($ FormWrap
    (d/button
     {:class title-style
      :onClick #(set-is-sign-up (not is-sign-up))}
     "Sign Up Form Placeholder")))

(defnc Home [props]
  {:wrap [with-keechma]}
  (let [[is-sign-up set-is-sign-up] (hooks/use-state false)
        email-value (mfui/use-get-in-data props :login-form :email)
        password-value (mfui/use-get-in-data props :login-form :password)
        inputs-empty? (and (empty? email-value) (empty? password-value))
        {:keys [submit-errors]} (use-sub props :login-form)]
    ($ HomeWrap
      (if is-sign-up
        ($ SignUpForm
          {:set-is-sign-up set-is-sign-up
           :is-sign-up is-sign-up})
        ($ FormWrap
          (d/h1 {:class title-style}
                "Log in")
          (d/p "Don't have an account? "
               ($ SignUpBtn
                 {:onClick #(set-is-sign-up (not is-sign-up))}
                 "Sign Up"))
          ($ FormContainer
            {:onSubmit #(do
                          (.preventDefault %)
                          (dispatch props :login-form :on-submit))}
            ($ InputGroupLogin)
            (d/div {:class "w-full"}
                   ($ ButtonDefaul {:additional-style "w-full flex justify-center"
                                    :label "Log In"
                                    :svg (inline "log-in.svg")
                                    :disabled inputs-empty?})
                   (when-not inputs-empty?
                     (d/div {:class error-msg-style}
                            submit-errors)))))))))
