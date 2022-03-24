(ns app.ui.pages.user
  (:require [helix.dom :as d]
            [helix.core :as hx :refer [$]]
            [keechma.next.controllers.router :as router]
            [keechma.next.helix.core :refer [with-keechma use-sub]]
            [keechma.next.helix.lib :refer [defnc]]
            [keechma.next.helix.classified :refer [defclassified]]
            [app.ui.components.datatable :refer [Datatable]]))

(defclassified HomepageWrapper :div "h-screen w-screen flex bg-gray-200")

(defnc UserRenderer [props]
  (let [user (use-sub props :current-user)]
    ($ HomepageWrapper

      (d/div {:class "flex flex-1 flex-col items-center justify-center m-16"}
             (d/a {:href (router/get-url props :router {:page "users"})}
                  (d/button {:class "inline-flex items-center px-6 py-3 border border-transparent text-base font-medium rounded shadow-sm text-white bg-cyan-600 hover:bg-cyan-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-cyan-500 shadow-lg shadow-cyan-500/50"}
                            "< USERS"))
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
                                    {:header/content "Inserted at"
                                     :cell/content   (fn [item]
                                                       (str (:users/inserted-at item)))}
                                    {:header/content "Updated at"
                                     :cell/content   (fn [item]
                                                       (str (:users/updated-at item)))}]
                           :data [user]})))))

(def User (with-keechma UserRenderer))

