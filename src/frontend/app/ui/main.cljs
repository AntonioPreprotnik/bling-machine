(ns app.ui.main
  (:require [app.ui.pages.home :refer [Home]]
            [app.ui.pages.user :refer [User]]
            [app.ui.pages.users :refer [Users]]
            [clojure.core.match :refer-macros [match]]
            [helix.core :as hx :refer [$]]
            [helix.dom :as d]
            [keechma.next.helix.core :refer [use-sub with-keechma]]
            [keechma.next.helix.lib :refer [defnc]]))

(defnc MainRenderer [props]
  (let [router (use-sub props :router)]
    (match [router]
      [{:page "home"}] ($ Home)
      [{:page "users" :id _}] ($ User)
      [{:page "users"}] ($ Users)
      :else (d/div "404"))))

(def Main (with-keechma MainRenderer))
