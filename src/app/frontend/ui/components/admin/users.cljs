(ns app.frontend.ui.components.admin.users
  (:require
   [app.frontend.ui.components.button :refer [ButtonDefaul]]
   [app.shared.util.inliner :as inliner :refer-macros [inline]]
   [helix.core :as hx :refer [$]]
   [helix.dom :as d]
   [keechma.next.helix.classified :refer [defclassified]]
   [keechma.next.helix.core :refer [dispatch use-sub with-keechma]]
   [keechma.next.helix.lib :refer [defnc]]))

(defclassified UsersTableContainer :div "border-2 border-gray-200 rounded-xl m-auto")
(defclassified UsersTableWrap :table " text-left overflow-hidden rounded-lg")
(defclassified TableHeaderDescriptions :tr "uppercase border-b-2 border-gray-200 bg-gray-200")
(defclassified TableDataCell :td "p-3"
  (fn [{:keys [idx]}]
    (when (odd? idx) " bg-gray-100")))
(defclassified EditUserWrap :td "p-3 rounded-rb hover:text-red-400"
  (fn [{:keys [idx]}]
    (when (odd? idx) " bg-gray-100")))
(defclassified EditUserBtn :button "disabled:bg-gray-400 disabled:opacity-50 disabled:text-black")

(def table-header-descriptions ["first 10 ID characters" "First Name" "Last Name" "Email" "ZIP" ""])

(defnc UsersTable [{:keys [users on-click]}]
  ($ UsersTableContainer
    ($ UsersTableWrap
      (d/thead
       ($ TableHeaderDescriptions
         (mapv
          #(d/th {:key   %
                  :class "p-3"} %)
          table-header-descriptions)))
      (map-indexed
       (fn [idx {:users/keys [first-name last-name email zip id]}]
         (d/tbody {:key idx}
                  (d/tr
                   ($ TableDataCell {:idx idx} (take 10 (str id)))
                   ($ TableDataCell {:idx idx} first-name)
                   ($ TableDataCell {:idx idx} last-name)
                   ($ TableDataCell {:idx idx} email)
                   ($ TableDataCell {:idx idx} zip)
                   ($ EditUserWrap {:idx idx}
                     ($ EditUserBtn {:onClick on-click}
                       (inline "edit-pencil.svg"))))))
       users))))

(defnc Users [props]
  {:wrap [with-keechma]}
  (let [users (use-sub props :users)
        on-open-add-user-modal #(dispatch props :modal-add-user :on)
        on-open-edit-modal #(dispatch props :modal-edit-user :on)]
    (d/div {:class "flex flex-col w-full"}
           ($ ButtonDefaul {:additional-style "ml-auto"
                            :label "Add User"
                            :svg (inline "add-user.svg")
                            :on-click on-open-add-user-modal})
           ($ UsersTable {:users users
                          :on-click on-open-edit-modal}))))
