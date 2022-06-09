(ns app.ui.components.pure.spinner
  (:require
   [app.util.inliner :as inliner :refer-macros [inline]]
   [helix.core :as hx :refer [$]]
   [helix.dom :as d]
   [keechma.next.helix.classified :refer [defclassified]]
   [keechma.next.helix.lib :refer [defnc]]))

(defclassified SpinnerWrap :div "w-full flex justify-center items-center")

(defnc Spinner [_]
  ($ SpinnerWrap
    (d/div  {:class "spinner"}
            (inline "spinner.svg"))))
