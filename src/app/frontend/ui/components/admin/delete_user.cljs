(ns app.frontend.ui.components.admin.delete-user
  (:require
   [app.frontend.ui.components.button :refer [ButtonDefaul]]
   [helix.core :as hx :refer [$ <>]]
   [helix.dom :as d]
   [keechma.next.helix.classified :refer [defclassified]]
   [keechma.next.helix.core :refer [dispatch use-sub with-keechma]]
   [keechma.next.helix.lib :refer [defnc]]))

(defclassified DeleteUserCopy :div "text-center text-lg")
(defclassified DeleteUserActionWrap :div "flex space-x-8")
(def delete-button-style "w-full flex justify-center my-4")

(defnc DeleteUser [props]
  {:wrap [with-keechma]}
  (let [{:users/keys [first-name last-name]} (:selected-user-data (use-sub props :selected-user))
        close-modal-delete-user              #(dispatch props :modal-delete-user :off)
        on-delete-user #(dispatch props :delete-user :on-delete-user)]
    (<>
      ($ DeleteUserCopy
        (d/p "Are you sure that want to delete user")
        (d/p (str first-name " " last-name "?")))
      ($ DeleteUserActionWrap
        (map-indexed
         (fn [idx {:keys [label on-click]}]
           ($ ButtonDefaul {:key idx
                            :additional-style delete-button-style
                            :label            label
                            :on-click         on-click}))
         [{:label "OK" :on-click on-delete-user}
          {:label "Cancel" :on-click close-modal-delete-user}])))))
