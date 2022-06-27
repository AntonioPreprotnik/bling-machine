(ns app.frontend.ui.components.button
  (:require
   [helix.core :as hx :refer [$]]
   [helix.dom :as d]
   [keechma.next.helix.classified :refer [defclassified]]
   [keechma.next.helix.lib :refer [defnc]]))

(defclassified ButtonDefaultWrap :button "flex justify-center py-2 px-3 border border-gray-400 rounded-lg hover:bg-gray-400 hover:text-white disabled:bg-gray-400 disabled:opacity-50 disabled:text-black")

(defnc ButtonDefaul [{:keys [label svg on-click disabled additional-style]}]
  ($ ButtonDefaultWrap {:class additional-style
                        :onClick on-click
                        :disabled disabled}
    (d/span label)
    (d/span {:class "ml-4"}
            svg)))
