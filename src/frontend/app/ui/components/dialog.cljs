(ns app.ui.components.dialog
  (:require
   ["@headlessui/react" :refer [Dialog] :rename {Dialog HUIDialog}]
   [app.util.inliner :as inliner :refer-macros [inline]]
   [applied-science.js-interop :as j]
   [helix.core :as hx :refer [$]]
   [helix.dom :as d]
   [keechma.next.helix.classified :refer [defclassified]]
   [keechma.next.helix.lib :refer [defnc]]))

(def DialogOverlay (j/get HUIDialog :Overlay))
(def DialogTitle (j/get HUIDialog :Title))

(def dialog "fixed inset-0 flex justify-center items-center font-sans")
(def dialog-overlay "absolute inset-0 bg-gray-900 opacity-50")
(def dialog-title "text-center text-lg font-bold w-full mt-4 ml-8")

(defclassified ModalWrap :div "bg-white relative shadow-xl w-1/3 rounded-2xl px-6 pt-6 pb-8 max-w-[528px]")
(defclassified DialogCrossWrap :div  "w-8 flex items-start justify-end shrink-0")

(defnc DialogCross [{:keys [onDismiss set-cross-style]}]
  ($ DialogCrossWrap
    (d/button
     {:onClick onDismiss}
     (inline "cross.svg"))))

(defnc Dialog
  [{:keys [children title onClose]}]
  (d/$d HUIDialog
        {:open true
         :onClose onClose
         :class dialog}
        (d/$d DialogOverlay
              {:class dialog-overlay})
        ($ ModalWrap
          (d/div {:class "flex mb-8"}
                 (d/$d DialogTitle
                       {:class dialog-title}
                       title)
                 ($ DialogCross {:onDismiss onClose}))
          children)))

(defnc Modal [{:keys [modal-title is-modal-open? close-modal children]}]
  (when is-modal-open?
    ($ Dialog {:title modal-title
               :onClose close-modal}
      children)))
