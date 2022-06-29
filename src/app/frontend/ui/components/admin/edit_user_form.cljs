(ns app.frontend.ui.components.admin.edit-user-form
  (:require
   [app.frontend.inputs :refer [wrapped-input]]
   [app.frontend.ui.components.button :refer [ButtonDefaul]]
   [app.frontend.ui.pages.home :refer [error-msg-style input-style]]
   [app.shared.util.inliner :as inliner :refer-macros [inline]]
   [helix.core :as hx :refer [$]]
   [helix.dom :as d]
   [keechma.next.helix.core :refer [dispatch with-keechma]]
   [keechma.next.helix.lib :refer [defnc]]))

(def edit-user-input
  [{:attr :first-name :placeholder "Edit first name"}
   {:attr :last-name :placeholder "Edit last name"}
   {:attr :zip :placeholder "Edit zip"}])

(defnc EditUserForm [props]
  {:wrap [with-keechma]}
  (let [controller :edit-user]
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
            ($ ButtonDefaul {:additional-style "w-full flex justify-center mt-4"
                             :label "Edit User"
                             :svg (inline "edit-pencil.svg")}))))
