(ns cache
  (:require
   [clojure.java.io :as io]
   [clojure.string :as str]))

(defn rename-file
  [old new]
  (.renameTo (io/file old) (io/file new)))

(defn clear-resources []
  (let [css (.listFiles (io/file  "resources/public/assets/css"))
        js (.listFiles (io/file "resources/public/assets/js"))]
    (doseq [css-file css]
      (io/delete-file css-file))
    (doseq [js-file js]
      (io/delete-file js-file))))

(defn add-timestamp []
  (let [time-stamp (quot (System/currentTimeMillis) 1000)
        style ["resources/public/assets/css/style.css" (format "resources/public/assets/css/style-%s.css"  time-stamp)]
        app  ["resources/public/assets/js/app.js" (format "resources/public/assets/js/app-%s.js"  time-stamp)]]
    (rename-file (first style) (second style))
    (rename-file (first app) (second app))
    (spit "resources/public/index.html"
          (-> (slurp "resources/public/index.tmpl")
              (str/replace  #"\*style\*" (format "style-%s.css"  time-stamp))
              (str/replace  #"\*app\*" (format "app-%s.js"  time-stamp))))))



