(ns app.frontend.ui.components.admin.delete-user
  (:require
   [app.frontend.inputs :refer [wrapped-input]]
   [app.frontend.ui.components.button :refer [ButtonDefaul]]
   [helix.core :as hx :refer [$ <>]]
   [helix.dom :as d]
   [keechma.next.helix.core :refer [dispatch with-keechma use-sub]]
   [keechma.next.helix.lib :refer [defnc]]))

(defnc DeleteUser [props]
  {:wrap [with-keechma]} 
  (let [{:users/keys [first-name last-name]} (:selected-user-data (use-sub props :selected-user))
        close-modal-delete-user              #(dispatch props :modal-delete-user :off)
        on-delete-user #(dispatch props :delete-user :on-delete-user)]
    (<>
      (d/div {:class "flex justify-center text-lg"} 
             (str "Are you sure that want to delete user " first-name " " last-name "?"))
      (d/div {:class "flex space-x-8"} 
             ($ ButtonDefaul {:additional-style "w-full flex justify-center mt-4"
                              :label            "OK"
                              :on-click         on-delete-user})
             ($ ButtonDefaul {:additional-style "w-full flex justify-center mt-4"
                              :label            "Cancel"
                              :on-click         close-modal-delete-user})))))
