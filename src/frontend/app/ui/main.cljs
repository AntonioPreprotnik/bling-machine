(ns app.ui.main
  (:require
   [app.ui.components.admin.dashboard :refer [Dashboard]]
   [app.ui.components.admin.users :refer [Users]]
   [app.ui.pages.admin.admin-panel-layout :refer [AdminPanelLayout]]
   [app.ui.pages.home :refer [Home]]
   [clojure.core.match :refer-macros [match]]
   [helix.core :as hx :refer [$]]
   [helix.dom :as d]
   [keechma.next.helix.core :refer [use-sub with-keechma]]
   [keechma.next.helix.lib :refer [defnc]]))

(defnc Main [props]
  {:wrap [with-keechma]}
  (let [{:keys [page subpage]} (use-sub props :router)]
    (match [page subpage]
      ["home" _] ($ Home)
      ["admin-panel" "users"] ($ AdminPanelLayout ($ Users))
      ["admin-panel" _] ($ AdminPanelLayout ($ Dashboard))
      :else (d/div "404"))))

