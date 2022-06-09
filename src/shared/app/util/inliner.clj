(ns app.util.inliner
  (:require
   [clojure.java.io :as io]
   [clojure.walk :refer [prewalk]]
   [helix.core]
   [hicada.compiler]
   [pl.danieljanus.tagsoup :as ts]))

(def rename-attr-mapping
  {:viewbox :viewBox
   :gradientunits :gradientUnits})

(defn rename-attrs [hiccup]
  (prewalk
   (fn [v]
     (if-let [renamed (rename-attr-mapping v)]
       renamed
       v))
   hiccup))

(defmacro inline [path]
  (let [src (-> path io/resource slurp)
        hiccup (-> src ts/parse-string rename-attrs)]
    (hicada.compiler/compile hiccup {:create-element 'helix.core/$
                                     :server-render? true
                                     {} &env})))

(comment
  (macroexpand-1 '(inline "primary-tent.svg")))
