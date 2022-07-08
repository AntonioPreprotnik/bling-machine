(ns app.frontend.ui.components.switch-user-role
  (:require
   [helix.core :as hx :refer [$]]
   [helix.dom :as d]
   [keechma.next.helix.classified :refer [defclassified]]
   [keechma.next.helix.lib :refer [defnc]]))

(defclassified SwitchUserText :div "text-md text-gray-900 capitalize")
(defclassified SwitchCheckboxWrap :button "relative inline-flex h-[38px] w-[74px] shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none focus-visible:ring-2 focus-visible:ring-white focus-visible:ring-opacity-75 ")
(defclassified SwitchCheckboxCircle :span "pointer-events-none inline-block h-[34px] w-[34px] transform rounded-full bg-white shadow-lg ring-0 transition duration-200 ease-in-out")

(defnc SwitchUserRole [{:keys [is-admin on-switch]}]
  (d/div {:class "flex justify-center items-center space-x-6"}
         ($ SwitchUserText
           (str "admin role " (if is-admin "on" "off")))
         ($ SwitchCheckboxWrap
           {:onClick on-switch
            :class [(if is-admin "bg-green-400" "bg-red-400")]}
           ($ SwitchCheckboxCircle
             {:aria-hidden true
              :class [(if is-admin "translate-x-9" "translate-x-0")]}))))
