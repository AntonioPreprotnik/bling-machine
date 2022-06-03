(ns app.inputs
  (:require
   [helix.core :as hx :refer [$]]
   [helix.dom :as d]
   [keechma.next.controllers.malli-form.ui :as mfui]
   [keechma.next.helix.core :refer [with-keechma]]
   [keechma.next.helix.lib :refer [defnc]]))
   
(defn get-element-props
  [default-props props]
  (let [element-props (into {} (filter (fn [[k _]] (simple-keyword? k)) props))]
    (reduce-kv
     (fn [m k v]
       (let [prev-v (get m k)
             val (cond (and (fn? prev-v) (fn? v))
                       (fn [& args]
                         (apply prev-v args)
                         (apply v args))
                       (and (= :class k) (:class m)) (flatten [v (:class m)])
                       :else v)]
         (assoc m k val)))
     default-props
     element-props)))

(defnc Errors [{:keechma.form/keys [controller] :input/keys [attr error-msg-style] :as props}]
  {:wrap [with-keechma]}
  (let [value (mfui/use-get-in-data props controller attr)]
    (when-let [errors (mfui/use-get-in-errors props controller attr)]
      (d/div
       {:class error-msg-style}
       (map-indexed
        (fn [i e] (d/div {:key i} e))
        (when-not (= "" value)
          errors))))))
    
(defnc TextInput [{:keechma.form/keys [controller] :input/keys [attr type style] :as props}]
  {:wrap [with-keechma]}
  (let [element-props (get-element-props {} props)
        value (mfui/use-get-in-data props controller attr)
        errors (mfui/use-get-in-errors props controller attr)]
    (d/input {:value (str value)
              :type  (name type)
              :onChange #(mfui/on-partial-change props controller attr (.. % -target -value))
              :onBlur #(mfui/on-commit-change props controller attr)
              :class style
              & (if (= errors [])
                  element-props
                  (assoc element-props :class style))})))

(defmulti input (fn [props] (:input/type props)))
(defmethod input :default [props] ($ TextInput {& props}))

(defmulti wrapped-input (fn [props] (:input/type props)))
(defmethod wrapped-input :default [props]
  (d/div
    (input props)
    ($ Errors {& props})))
