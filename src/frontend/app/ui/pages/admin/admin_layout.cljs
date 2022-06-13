(ns app.ui.pages.admin.admin-layout
  (:require
   [app.inputs :refer [wrapped-input]]
   [app.ui.components.admin.sidebar :refer [Sidebar]]
   [app.ui.components.button :refer [ButtonDefaul]]
   [app.ui.components.dialog :refer [Modal]]
   [app.ui.components.spinner :refer [Spinner]]
   [app.ui.pages.home :refer [error-msg-style input-style]]
   [app.util.inliner :as inliner :refer-macros [inline]]
   [helix.core :as hx :refer [$ suspense]]
   [helix.dom :as d]
   [keechma.next.helix.classified :refer [defclassified]]
   [keechma.next.helix.core :refer [dispatch use-sub with-keechma]]
   [keechma.next.helix.lib :refer [defnc]]))

(defclassified AdminPanelWrap :div "h-screen w-screen flex")
(defclassified AdminPanelInner :div "max-h-full h-full w-full absolute flex overflow-y-auto py-4 pr-6")

(def add-user-input
  [{:attr :email :placeholder "Enter Email"}
   {:attr :first-name :placeholder "Enter First Name"}
   {:attr :last-name :placeholder "Enter Last Name"}
   {:attr :zip :placeholder "Enter Zip Code"}])

(defnc AddUserModal [props]
  {:wrap [with-keechma]}
  (let [controller :create-user]
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
                             :svg (inline "add-user.svg")}))))

(defnc AdminLayout [{:keys [children] :as props}]
  {:wrap [with-keechma]}
  (let [is-modal-add-user-open? (use-sub props :modal-add-user)
        close-modal-add-user #(dispatch props :modal-add-user :off)
        is-modal-edit-user-open? (use-sub props :modal-edit-user)
        close-modal-edit-user #(dispatch props :modal-edit-user :off)]
    ($ AdminPanelWrap
      ($ Modal {:modal-title "Add User"
                :is-modal-open? is-modal-add-user-open?
                :close-modal close-modal-add-user}
        ($ AddUserModal))
      ($ Modal {:modal-title "Edit User"
                :is-modal-open? is-modal-edit-user-open?
                :close-modal close-modal-edit-user}
        (d/div {:class "text-center"}
               "Edit User Form Content"))
      ($ Sidebar)
      (d/div {:class "flex-grow relative"}
             ($ AdminPanelInner
               (suspense
                {:fallback ($ Spinner)}
                children))))))
