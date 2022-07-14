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
  (let [{:keys [page subpage]} (use-sub props :router)
        role (use-sub props :role)]
    (match [role page subpage]
      [:admin "admin" "users"] ($ AdminLayout ($ Users))
      [:admin "admin" _] ($ AdminLayout ($ Dashboard))
      [:anon "home" _] ($ Home)
      :else (d/div "404"))))

