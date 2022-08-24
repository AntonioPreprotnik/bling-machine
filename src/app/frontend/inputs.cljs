(ns app.frontend.inputs
  (:require
   ["@headlessui/react" :refer [Listbox]]
   ["react" :refer [forwardRef]]
   [app.shared.util.inliner :as inliner :refer-macros [inline]]
   [applied-science.js-interop :as j]
   [helix.core :as hx :refer [$]]
   [helix.dom :as d]
   [keechma.next.controllers.malli-form.ui :as mfui]
   [keechma.next.helix.classified :refer [defclassified]]
   [keechma.next.helix.core :refer [with-keechma]]
   [keechma.next.helix.lib :refer [defnc]]))

(defn get-element-props
  [default-props props]
  (let [element-props (into {} (filter (fn [[k _]] (simple-keyword? k)) props))]
    (reduce-kv
     (fn [m k v]
       (let [prev-v (get k m)
             val (cond (and (fn? prev-v) (fn? v))
                       (fn [& args] (apply prev-v args) (apply v args))
                       (and (= :class k) (:class m)) (flatten [v (:class m)])
                       :else v)]
         (assoc m k val)))
     default-props
     element-props)))

;; DROPDOWN INPUT
(defclassified ListboxWrap :div "relative bg-white rounded-sm capitalize")
(defclassified ListboxItem :div "w-full text-left p-3 hover:bg-primary-7")
(def listbox-options "z-10 bg-white rounded-sm absolute w-full overflow-auto shadow-soft mt-1")
(def list-box-button "w-full flex items-center justify-between p-3 border-2 rounded-sm")

(def ListboxButton (j/get Listbox "Button"))
(def ListboxOptions (j/get Listbox "Options"))
(def ListboxOption (j/get Listbox "Option"))

(defnc Dropdown [{:keys [options selected on-change]}]
  (let [selected-option (some #(when (= selected (:value %)) %) options)]
    (d/$d Listbox {:value selected :onChange on-change}
          (j/fn [^:js {:keys [open]}]
            ($ ListboxWrap
              (d/$d ListboxButton {:className (str list-box-button
                                                   (if open " border-blue-1" " border-grey-3"))}
                    (d/div {:class ["text-left line-clamp-2" (when-not selected "opacity-50")]}
                           (:label selected-option))
                    (d/div {:class ["ml-2 ease-in-out duration-300" (when open "rotate-180")]}
                           (inline "chevrone-down.svg")))
              (d/$d ListboxOptions {:className listbox-options}
                    (map (fn [{:keys [label value]}]
                           ($ ListboxOption  {:key (str "key" value) :value value}
                             (j/fn [^:js {:keys [active]}]
                               ($ ListboxItem {:class [(when active "bg-primary-7") (when-not value "opacity-50")]}
                                 label))))
                         options)))))))

(defnc Amount [{:keys [options selected on-change]}]
  (let [selected-option (some #(when (= selected (:value %)) %) options)]
    (d/$d Listbox {:value selected :onChange on-change}
          (j/fn [^:js {:keys [open]}]
            ($ ListboxWrap
              (d/$d ListboxButton {:className (str list-box-button
                                                   (if open " border-blue-1" " border-grey-3"))}
                    (d/div {:class ["text-left line-clamp-2" (when-not selected "opacity-50")]}
                           (:label selected-option))
                    (d/div {:class ["ml-2 ease-in-out duration-300" (when open "rotate-180")]}
                           (inline "chevrone-down.svg")))
              (d/$d ListboxOptions {:className listbox-options}
                    (map (fn [{:keys [label value]}]
                           ($ ListboxOption  {:key (str "key" value) :value value}
                             (j/fn [^:js {:keys [active]}]
                               ($ ListboxItem {:class [(when active "bg-primary-7") (when-not value "opacity-50")]}
                                 label))))
                         options)))))))

;; DROPDOWN INPUT WRAP
(defnc DropdownWrap [{:keechma.form/keys [controller] :input/keys [attr] :as props} ref]
  {:wrap [forwardRef with-keechma]}
  (let [value (mfui/use-get-in-data props controller attr)
        modify-text (:modify-text props identity)
        default-props {:selected value
                       :on-change #(let [value (modify-text %)]
                                     (mfui/on-partial-change props controller attr value))}
        element-props (get-element-props default-props props)
        placeholder  (:placeholder element-props)
        element-props' (cond-> element-props
                         placeholder (update :options
                                             #(-> (apply list %)
                                                  (conj {:label placeholder :value nil}))))]
    ($ Dropdown {:ref ref & element-props'})))

;; SELECT INPUT
(defn render-options [options]
  (map
   (fn [{:keys [value label]}]
     (d/option {:key value :value value} label))
   options))

(defnc Select [{:keechma.form/keys [controller]
                :input/keys [attr]
                :as props}]
  {:wrap [with-keechma]}
  (let [value (mfui/use-get-in-data props controller attr)

        default-props {:value (or value "")
                       :onChange #(mfui/on-atomic-change props controller attr (.. % -target -value))}
        element-props (get-element-props default-props props)
        errors (mfui/use-get-in-errors props controller attr)
        {:keys [options optgroups placeholder]} props]
    (d/select {& (if (= errors [])
                   element-props
                   (assoc element-props :class "w-full p-2 bg-transparent rounded border border-solid shadow-md shadow-red border-red-500"))}
              (when placeholder
                (d/option {:label placeholder :value ""} placeholder))
              (if optgroups
                (map
                 (fn [{:keys [label options]}]
                   (d/optgroup {:key label :label label :value value}
                               (render-options options)))
                 optgroups)
                (render-options (remove #(= % {:value placeholder :label placeholder}) options))))))

(defnc Errors [{:keechma.form/keys [controller], :input/keys [attr], :as props}]
  {:wrap [with-keechma]}
  (when-let [errors (mfui/use-get-in-errors props controller attr)]
    (d/ul {:class "text-red-400 text-xs italic"}
          (map-indexed
           (fn [i e] (d/li {:key i} e))
           errors))))

;; TEXT-AREA INPUT
(defnc TextArea [{:keechma.form/keys [controller]
                  :input/keys [attr type]
                  :as props}]
  {:wrap [with-keechma]}
  (let [element-props (get-element-props {} props)
        value (mfui/use-get-in-data props controller attr)
        errors (mfui/use-get-in-errors props controller attr)]
    (d/textarea {:value (str value)
                 :rows "8"
                 :onChange #(mfui/on-partial-change props controller attr (.. % -target -value))
                 :onBlur #(mfui/on-commit-change props controller attr)
                 & (if (= errors [])
                     element-props
                     (assoc element-props :class "w-full p-2 bg-transparent rounded border border-solid shadow-md shadow-red border-red-500"))})))

;; TEXT INPUT
(defnc Text [{:keechma.form/keys [controller]
              :input/keys [attr type]
              :as props}]
  {:wrap [with-keechma]}
  (let [element-props (get-element-props {} props)
        value (mfui/use-get-in-data props controller attr)
        errors (mfui/use-get-in-errors props controller attr)]
    (d/input {:value (str value)
              :type  (name type)
              :onChange #(mfui/on-partial-change props controller attr (.. % -target -value))
              :onBlur #(mfui/on-commit-change props controller attr)
              & (if (= errors [])
                  element-props
                  (assoc element-props :class "p-4 rounded-lg border border-solid shadow-red border-red-500"))})))

;; RADIO GROUP INPUT

(defn- radio-input [answer]
  (case answer
    -2 "Strongly disagree"
    -1 "Disagree"
    0 "Neutral"
    1 "Agree"
    2 "Strongly agree"))

(defnc Radio [{:keechma.form/keys [controller]
               :question-data/keys [question-id]
               :input/keys [attr]
               :as props}]
  {:wrap [with-keechma]}
  (let [element-props (get-element-props {} props)
        errors (mfui/use-get-in-errors props controller attr)
        data (mfui/use-get-in-data props controller attr)]
    (d/div {:class "flex flex-row w-full justify-around"}
           (map (fn [answer]
                  (d/div {:key (str answer question-id)
                          :class "flex space-x-3"}
                         (d/label {:class "flex flex-col text-lg font-light cursor-pointer pl-2 items-center"}
                                  (d/input {:type "radio"
                                            :class "mr-4"
                                            :onChange #(mfui/on-partial-change props controller attr (.. % -target -value))
                                            :checked (= (str data) (str answer))
                                            :value answer
                                            :id (str "id" question-id)
                                            :key (str "key" question-id)
                                            :name question-id
                                            & (if (= errors [])
                                                element-props
                                                (assoc element-props :class "w-full p-2 bg-transparent rounded border border-solid shadow-md shadow-red border-red-500"))})
                                  (d/p {:class "text-sm"}
                                       (radio-input answer)))))
                [-2 -1 0 1 2]))))

;; SLIDER INPUT

(defnc Slider [{:keys [key]
                :keechma.form/keys [controller]
                :label/keys [text]
                :input/keys [attr min max step]
                :as props}]
  {:wrap [with-keechma]}
  (let [value (mfui/use-get-in-data props controller attr)]
    (d/label {:class "flex flex-col text-lg"}
             (d/div {:class "flex flex-row justify-between"}
                    (d/span text)
                    (d/span value))
             (d/input {:class "flex cursor-pointer"
                       :type "range"
                       :id (str "id" key)
                       :name text
                       :value value
                       :onChange #(mfui/on-partial-change props controller attr (.. % -target -value))
                       :min min
                       :max max
                       :step (if step step 1)}))))

(defmulti input (fn [props] (:input/type props)))
(defmethod input :text [props] ($ Text {& props}))
(defmethod input :textarea [props] ($ TextArea {& props}))
(defmethod input :password [props] ($ Text {:input/type :password & props}))
(defmethod input :select [props] ($ Select {& props}))
(defmethod input :radio [props] ($ Radio {& props}))
(defmethod input :slider [props] ($ Slider {& props}))
(defmethod input :dropdown [props] ($ DropdownWrap {& props}))

(defnc LabelRenderer [{:keys [text]}]
  (d/label text))

(defmulti wrapped-input (fn [props] (:input/type props)))
(defmethod wrapped-input :slider [{:label/keys [text] :fieldset/keys [style] :as props}]
  (d/fieldset {:class (if style style "")}
              (input (assoc props :class "w-full p-2 bg-transparent rounded border border-solid shadow-md"))
              ($ Errors {& props})))

(defmethod wrapped-input :default [{:label/keys [text] :fieldset/keys [style] input-style :input/style :as props}]
  (d/fieldset {:class (if style style "")}
              (when text ($ LabelRenderer {:text text}))
              (input (assoc props :class (if input-style input-style "w-full p-2 bg-transparent rounded border border-solid shadow-md")))
              ($ Errors {& props})))

