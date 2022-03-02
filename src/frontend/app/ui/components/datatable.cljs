(ns app.ui.components.datatable
  (:require [helix.dom :as d]
            [keechma.next.helix.classified :refer [defclassified]]
            [helix.core :as hx :refer [$]]
            [clojure.string :as str]
            [keechma.next.helix.lib :refer [defnc]]))

(defn process-col-classes [classes]
  (if (string? classes)
    classes
    (str/join " " (map name classes))))

(defn last-column? [config idx]
  (= (inc idx) (count config)))

(defn middle-column? [config idx]
  (and (not (zero? idx))
       (not (last-column? config idx))))

(defclassified TableHeader :thead "items-center border-transparent")
(defclassified TableHeaderRow :tr "flex flex-col sm:table-row text-left border-transparent md:border-l-8")
(defclassified TableHeaderCell :th "pl-1 py-2 sm:py-5 text-xs text-filterdarkblue uppercase font-bold min-h-48"
  (fn [{variant :th/variant}]
    (case variant
      "first" "pl-2"
      "last" "pr-2"
      "inner" "px-1"
      "")))

(defclassified TableBody :tbody "flex-1 sm:flex-none")
(defclassified TableBodyRow :tr "flex flex-col sm:table-row md:hover:border-blue md:border-l-8 border-transparent")
(defclassified TableBodyCell :td "py-2 lg:py-4 text-filterdarkblue text-xs lg:text-sm truncate border-transparent"
  (fn [{variant :th/variant}]
    (case variant
      "first" "pl-2"
      "last" "pr-2"
      "inner" "px-1"
      "")))

(defn determine-td-variant [idx config]
  (cond
    (zero? idx) "first"
    (last-column? config idx) "last"
    (middle-column? config idx) "middle"
    :else nil))

(defnc THead [{:keys [config data]}]
  ($ TableHeader
     ($ TableHeaderRow
        (map-indexed
         (fn [_idx c]
           (let [header-content (:header/content c)]
             ($ TableHeaderCell {:scope "col"
                                 :key (or (:header/key c) (:key c) (when (string? header-content) header-content))
                                 :on-click  (:header/on-click c)
                                 :class [(process-col-classes (:header/class c))]}
                (d/div (:header/content c)))))
         config))))

(defnc TBody [{:keys [config data]}]
  ($ TableBody
     (map-indexed
      (fn [_idx d]
        ($ TableBodyRow {:key (or (:row/key d)
                                  (:id d)
                                  (:criterion d)
                                  (:resourceName d))
                         :on-click (:row/on-click d)}
           (map-indexed
            (fn [idx c]
              (let [header-content (:header/content c)
                    cell-key       (or (:cell/key c) (:key c) (when (string? header-content) header-content))
                    cell-content   (:cell/content c)]
                ($ TableBodyCell {:key cell-key
                                  :td/variant (determine-td-variant idx config)
                                  :class [(process-col-classes (:cell/class c))]}
                   (cond
                     (keyword? cell-content) (get d cell-content)
                     (vector? cell-content) (conj cell-content d)
                     (fn? cell-content) (cell-content d)
                     :else cell-content))))
            config)))
      data)))

(defnc Datatable [{:keys [config data]}]
  (let [config' (filterv (complement nil?) config)]
    (d/div {:class "mt-5 shadow-md bg-white w-full"}
           (d/table {:class "overflow w-full divide-y divide-gray-200 rounded-md"}
                    ($ THead {:config config' :data data})
                    ($ TBody {:config config' :data data})))))
