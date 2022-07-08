(ns app.frontend.ui.components.password-field-secured
  (:require
   [app.frontend.inputs :refer [wrapped-input]]
   [app.frontend.ui.components.shared-style :refer [error-msg-style
                                                    input-style]]
   [app.shared.util.inliner :as inliner :refer-macros [inline]]
   [helix.core :as hx :refer [$]]
   [helix.dom :as d]
   [helix.hooks :as hooks]
   [keechma.next.helix.classified :refer [defclassified]]
   [keechma.next.helix.lib :refer [defnc]]))

(defclassified PasswordFieldWrap :div "w-full relative")

(defnc PasswordFieldSecured [{:keys [controller placeholder input-value]}]
  (let [[password-mask set-password-mask] (hooks/use-state true)]
    ($ PasswordFieldWrap
      (wrapped-input {:keechma.form/controller controller
                      :input/style input-style
                      :input/error-msg-style error-msg-style
                      :input/type (if password-mask :password :text)
                      :input/attr :password
                      :placeholder placeholder})
      (when (and input-value (not= "" input-value))
        (d/button {:type "button"
                   :onClick #(set-password-mask not)
                   :style {:position "absolute"
                           :right 10
                           :top 12}}
                  (if password-mask
                    (inline "security-eye-slash.svg")
                    (inline "security-eye.svg")))))))
