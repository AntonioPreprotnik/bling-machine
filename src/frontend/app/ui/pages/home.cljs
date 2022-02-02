(ns app.ui.pages.home
  (:require [helix.dom :as d]
            [helix.core :as hx :refer [$]]
            [keechma.next.helix.core :refer [with-keechma use-sub]]
            [keechma.next.helix.lib :refer [defnc]]
            [keechma.next.helix.classified :refer [defclassified]]))

(defclassified HomepageWrapper :div "h-screen w-screen flex bg-gray-200")

(defnc HomeRenderer [props]
  (let [#_current-user #_(use-sub props :current-user)])
  ($ HomepageWrapper

    (d/div {:class "flex flex-1 flex-col items-center justify-center px-2"}
      (d/span "Keechma!"))))

(def Home (with-keechma HomeRenderer))

