(ns app.frontend.ui.main
  (:require
   [app.frontend.ui.components.admin.dashboard :refer [Dashboard]]
   [app.frontend.ui.components.admin.users :refer [Users]]
   [app.frontend.ui.pages.admin.admin-layout :refer [AdminLayout]]
   [app.frontend.ui.pages.home :refer [Home]]
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
      ["admin" "users"] ($ AdminLayout ($ Users))
      ["admin" _] ($ AdminLayout ($ Dashboard))
      :else (d/div "404"))))
