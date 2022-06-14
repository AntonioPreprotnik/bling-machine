(ns frontend.ui.main
  (:require
   [clojure.core.match :refer-macros [match]]
   [frontend.ui.pages.admin-panel :refer [AdminPanel]]
   [frontend.ui.pages.home :refer [Home]]
   [frontend.ui.pages.user :refer [User]]
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
