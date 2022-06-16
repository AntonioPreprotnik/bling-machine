(ns app.frontend.ui.main
  (:require
   [app.frontend.ui.pages.admin-panel :refer [AdminPanel]]
   [app.frontend.ui.pages.home :refer [Home]]
   [app.frontend.ui.pages.user :refer [User]]
   [clojure.core.match :refer-macros [match]]
   [helix.core :as hx :refer [$]]
   [helix.dom :as d]
   [keechma.next.helix.core :refer [use-sub with-keechma]]
   [keechma.next.helix.lib :refer [defnc]]))

(defnc MainRenderer [props]
  (let [router (use-sub props :router)]
    (match [router]
      [{:page "home"}] ($ Home)
      [{:page "admin-panel" :id _}] ($ User)
      [{:page "admin-panel"}] ($ AdminPanel)
      :else (d/div "404"))))

(def Main (with-keechma MainRenderer))
