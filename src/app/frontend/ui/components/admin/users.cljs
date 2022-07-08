(ns app.frontend.ui.components.admin.users
  (:require
   [app.frontend.ui.components.button :refer [ButtonDefault]]
   [app.shared.util.inliner :as inliner :refer-macros [inline]]
   [helix.core :as hx :refer [$]]
   [helix.dom :as d]
   [keechma.next.helix.classified :refer [defclassified]]
   [keechma.next.helix.core :refer [dispatch use-sub with-keechma]]
   [keechma.next.helix.lib :refer [defnc]]))

(defclassified UsersTableContainer :div "border-2 border-gray-200 rounded-xl m-auto")
(defclassified UsersTableWrap :table " text-left overflow-hidden rounded-lg")
(defclassified TableHeaderDescriptions :tr "uppercase border-b-2 border-gray-200 bg-gray-200")
(defclassified TableDataCell :td "p-3 text-center"
  (fn [{:keys [idx]}]
    (when (odd? idx) " bg-gray-100")))
(defclassified UserActionWrap :td "p-3 rounded-rb hover:text-red-400 text-center"
  (fn [{:keys [idx]}]
    (when (odd? idx) " bg-gray-100")))
(defclassified UserActionBtn :button "disabled:bg-gray-400 disabled:opacity-50 disabled:text-black")

(def table-header-descriptions ["user ID" "First Name" "Last Name" "Email" "Admin" "Edit" "Delete"])

(defnc UsersTable [{:keys [users on-click-edit-pencil on-click-trash]}]
  ($ UsersTableContainer
    ($ UsersTableWrap
      (d/thead
       ($ TableHeaderDescriptions
         (mapv
          #(d/th {:key   %
                  :class "p-3 text-center"} %)
          table-header-descriptions)))
      (map-indexed
       (fn [idx {:users/keys [first-name last-name email id is-admin] :as m}]
         (d/tbody {:key idx}
                  (d/tr
                   ($ TableDataCell {:idx idx} (str id))
                   ($ TableDataCell {:idx idx} first-name)
                   ($ TableDataCell {:idx idx} last-name)
                   ($ TableDataCell {:idx idx} email)
                   ($ TableDataCell {:idx idx} (if is-admin "Yes" "No"))
                   ($ UserActionWrap {:idx idx}
                     ($ UserActionBtn {:onClick #(on-click-edit-pencil m)}
                       (inline "edit-pencil.svg")))
                   ($ UserActionWrap {:idx idx}
                     ($ UserActionBtn {:onClick #(on-click-trash m)}
                       (inline "trash.svg"))))))
       users))))

(defnc Users [props]
  {:wrap [with-keechma]}
  (let [users (use-sub props :users)
        on-open-add-user-modal #(dispatch props :modal-add-user :on)
        on-open-edit-modal #(do (dispatch props :modal-edit-user :on)
                                (dispatch props :selected-user :on-select-user %))
        on-open-delete-user-modal #(do (dispatch props :modal-delete-user :on)
                                       (dispatch props :selected-user :on-select-user %))]
    (d/div {:class "flex flex-col w-full"}
           ($ ButtonDefault {:additional-style "ml-auto w-40"
                             :label "Add User"
                             :svg (inline "add-user.svg")
                             :on-click on-open-add-user-modal})
           ($ UsersTable {:users users
                          :on-click-edit-pencil on-open-edit-modal
                          :on-click-trash on-open-delete-user-modal}))))
