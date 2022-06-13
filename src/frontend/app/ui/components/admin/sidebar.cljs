(ns app.ui.components.admin.sidebar
  (:require
   [app.util.inliner :as inliner :refer-macros [inline]]
   [helix.core :as hx :refer [$]]
   [helix.dom :as d]
   [keechma.next.controllers.router :as router]
   [keechma.next.helix.classified :refer [defclassified]]
   [keechma.next.helix.core :refer [use-sub with-keechma]]
   [keechma.next.helix.lib :refer [defnc]]))

(defclassified SideBarContainer :aside "overflow-y-auto shrink-0 w-96 h-full py-4 px-6")
(defclassified SideBarWrap :div "bg-gray-200 rounded-lg h-full p-4 text-gray-900")
(defclassified BorderSeparator :div "border-b border-white w-full")

(def sidebar-items
  [{:page "admin" :svg (inline "dashboard.svg") :label "Dashboard"}
   {:page "admin" :subpage "users" :svg (inline "user.svg") :label "Users"}
   {:page "home" :subpage false :svg (inline "log-out.svg") :label "Log Out"}])

(defnc SidebarItem [props]
  {:wrap [with-keechma]}
  (let [router-subpage (-> (use-sub props :router) :subpage)]
    (map-indexed
     (fn [i {:keys [href svg label page subpage]}]
       (d/div {:key i}
              (d/a {:href (if subpage
                            (router/get-url props :router {:page page :subpage subpage})
                            (router/get-url props :router {:page page}))
                    :class ["flex p-4" (when (= router-subpage subpage) "text-gray-400")]}
                   (d/span svg)
                   (d/span {:class "pl-4"}
                           label))
              (when-not (= i 3)
                ($ BorderSeparator))))
     sidebar-items)))

(defnc Sidebar [_]
  ($ SideBarContainer
    ($ SideBarWrap
      ($ SidebarItem))))
