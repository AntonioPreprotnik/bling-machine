(ns app.ui.main
  (:require [keechma.next.helix.core :refer [with-keechma use-sub]]
            [keechma.next.helix.lib :refer [defnc]]
            [helix.core :as hx :refer [$]]
            [helix.dom :as d]
            [clojure.core.match :refer-macros [match]]
            [app.ui.pages.home :refer [Home]]
            [app.ui.pages.users :refer [Users]]
            [app.ui.pages.user :refer [User]]))

(defnc MainRenderer [props]
  (let [router (use-sub props :router)]
    (match [router]
      [{:page "home"}] ($ Home)
      [{:page "users" :id _}] ($ User)
      [{:page "users"}] ($ Users)
      :else (d/div "404"))))

(def Main (with-keechma MainRenderer))