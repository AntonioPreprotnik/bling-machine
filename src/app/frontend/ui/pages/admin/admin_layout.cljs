(ns app.frontend.ui.pages.admin.admin-layout
  (:require
   [app.frontend.ui.components.admin.add-user-form :refer [AddUserForm]]
   [app.frontend.ui.components.admin.edit-user-form :refer [EditUserForm]]
   [app.frontend.ui.components.admin.delete-user :refer [DeleteUser]]
   [app.frontend.ui.components.admin.sidebar :refer [Sidebar]]
   [app.frontend.ui.components.dialog :refer [Modal]]
   [app.frontend.ui.components.spinner :refer [Spinner]]
   [helix.core :as hx :refer [$ <> suspense]]
   [helix.dom :as d]
   [keechma.next.helix.classified :refer [defclassified]]
   [keechma.next.helix.core :refer [dispatch use-sub with-keechma]]
   [keechma.next.helix.lib :refer [defnc]]))

(defclassified AdminPanelWrap :div "h-screen w-screen flex")
(defclassified AdminPanelInner :div "max-h-full h-full w-full absolute flex overflow-y-auto py-4 pr-6")

(defnc AdminLayout [{:keys [children] :as props}]
  {:wrap [with-keechma]}
  (let [is-modal-add-user-open? (use-sub props :modal-add-user)
        close-modal-add-user #(dispatch props :modal-add-user :off)
        is-modal-edit-user-open? (use-sub props :modal-edit-user)
        close-modal-edit-user #(dispatch props :modal-edit-user :off)
        is-modal-delete-open? (use-sub props :modal-delete-user)
        close-modal-delete-user #(dispatch props :modal-delete-user :off)]
    ($ AdminPanelWrap
      ($ Modal {:modal-title "Add User"
                :is-modal-open? is-modal-add-user-open?
                :close-modal close-modal-add-user}
        ($ AddUserForm))
      ($ Modal {:modal-title "Edit User"
                :is-modal-open? is-modal-edit-user-open?
                :close-modal close-modal-edit-user}
        ($ EditUserForm))
      ($ Modal {:modal-title "Delete confirmation"
                :is-modal-open? is-modal-delete-open?
                :close-modal close-modal-delete-user}
        ($ DeleteUser))
      ($ Sidebar)
      (d/div {:class "flex-grow relative"}
             ($ AdminPanelInner
               (suspense
                {:fallback ($ Spinner)}
                children))))))
