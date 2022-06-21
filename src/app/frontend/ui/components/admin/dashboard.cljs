(ns app.ui.components.admin.dashboard
  (:require
   [helix.dom :as d]
   [keechma.next.helix.lib :refer [defnc]]))

(defnc Dashboard [_]
  (d/div {:class "flex items-center justify-center w-full text-2xl"}
         "Let's get started!"))
