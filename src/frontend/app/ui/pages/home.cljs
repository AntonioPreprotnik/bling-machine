(ns app.ui.pages.home
  (:require
   [helix.core :as hx :refer [$]]
   [helix.dom :as d]
   [keechma.next.controllers.router :as router]
   [keechma.next.helix.classified :refer [defclassified]]
   [keechma.next.helix.core :refer [with-keechma]]
   [keechma.next.helix.lib :refer [defnc]]))

(defclassified HomepageWrapper :div "h-screen w-screen flex bg-gray-200")

(defnc HomeRenderer [props]
  ($ HomepageWrapper
    (d/div {:class "max-w-2xl mx-auto text-center py-16 px-4 sm:py-20 sm:px-6 lg:px-8"}
           (d/div {:class "text-center"}
                  (d/h2 {:class "text-3xl font-extrabold sm:text-4xl"}
                        (d/span {:class "block"} "KEECHMA")))
           (d/div {:class "mt-8 flex justify-center"}
                  (d/div {:class "inline-flex rounded-md shadow"}
                         (d/a {:class "inline-flex items-center justify-center px-5 py-3 border border-transparent text-base font-medium rounded-md text-white bg-blue-600 hover:bg-indigo-700"
                               :href (router/get-url props :router {:page "users"})}
                              "Users"))))))

(def Home (with-keechma HomeRenderer))
