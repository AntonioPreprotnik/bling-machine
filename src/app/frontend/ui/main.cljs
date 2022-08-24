(ns app.frontend.ui.main
  (:require
   [app.frontend.ui.pages.currencies :refer [Currencies]]
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
      [:anon "home" _] ($ Currencies)
      :else (d/div "404"))))

