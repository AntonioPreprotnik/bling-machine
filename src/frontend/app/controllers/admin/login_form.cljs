(ns app.controllers.admin.login-form
  (:require
   [keechma.malli-forms.core :as mf]
   [keechma.next.controller :as ctrl]
   [keechma.next.controllers.malli-form :as mfc]
   [keechma.next.controllers.pipelines :as pipelines]
   [keechma.next.controllers.router :as router]
   [keechma.pipelines.core :as pp :refer-macros [pipeline!]]
   [schema :as schema]))

(derive :login-form ::pipelines/controller)

(def form (mf/make-form schema/registry :app.input.login {:email "" :password ""}))
(def login-credentials {:email "admin@vbt.com" :password "vbtadmin"})

(def initial-login-form
  (pipeline! [value {:keys [meta-state* schema]}]
    (pp/swap! meta-state* mfc/init-form form)))

(def submit-harcode-login-credentials
  (pipeline! [value {:keys [state*] :as ctrl}]
    (if (= login-credentials value)
      (router/redirect! ctrl :router {:page "admin"})
      (pp/swap! state* assoc-in [:login-error-msg] "Invalid credentials"))))

(def pipelines
  (merge
   mfc/pipelines
   {:keechma.on/start initial-login-form
    :keechma.form/on-partial-change (pipeline! [value {:keys [state*]}]
                                      mfc/on-partial-change)
    :on-submit submit-harcode-login-credentials}))

(defmethod ctrl/prep :login-form [ctrl]
  (pipelines/register ctrl pipelines))
