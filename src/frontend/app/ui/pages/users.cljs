(ns app.ui.pages.users
  (:require
   [app.ui.components.datatable :refer [Datatable]]
   [helix.core :as hx :refer [$]]
   [helix.dom :as d]
   [keechma.next.controllers.router :as router]
   [keechma.next.helix.classified :refer [defclassified]]
   [keechma.next.helix.core :refer [use-sub with-keechma]]
   [keechma.next.helix.lib :refer [defnc]]))

(defclassified HomepageWrapper :div "h-screen w-screen flex bg-gray-200")

(defnc UsersRenderer [props]
  (let [users (use-sub props :users)]
    ($ HomepageWrapper

      (d/div {:class "flex flex-1 flex-col items-center justify-center m-16"}
             (d/a {:href (router/get-url props :router {:page "home"})}
                  (d/button {:class "inline-flex items-center px-6 py-3 border border-transparent text-base font-medium rounded shadow-sm text-white bg-cyan-600 hover:bg-cyan-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-cyan-500 shadow-lg shadow-cyan-500/50"}
                            "< HOME"))
             ($ Datatable {:config [{:header/content "ID"
                                     :cell/content   (fn [item]
                                                       (str (:users/id item)))}
                                    {:header/content "First Name"
                                     :cell/content :users/first-name}
                                    {:header/content "Last Name"
                                     :cell/content :users/last-name}
                                    {:header/content "Email"
                                     :cell/content :users/email}
                                    {:header/content "ZIP"
                                     :cell/content :users/zip}
                                    {:header/content ""
                                     :cell/content   (fn [item]
                                                       (d/a {:href (router/get-url props :router {:page "users" :id (:users/id item)})}
                                                            (d/button {:class "inline-flex items-center px-2.5 py-1.5 border border-transparent text-xs font-medium rounded shadow-sm text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"}
                                                                      "Edit")))}]
                           :data users})))))

(def Users (with-keechma UsersRenderer))

