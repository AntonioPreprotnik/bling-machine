(ns frontend-version-patcher
  (:require
   [clojure.java.io :as io]
   [clojure.string :as str]))

(defn rename-file [old new]
  (.renameTo (io/file old) (io/file new)))

(defn clear-resources []
  (let [css-files (.listFiles (io/file "resources/public/assets/css"))
        js-files (.listFiles (io/file "resources/public/assets/js"))
        assets-files (concat css-files js-files)]
    (doseq [file assets-files]
      (io/delete-file file))))

(defn patch-prod-versions []
  (let [timestamp (quot (System/currentTimeMillis) 1000)
        css ["resources/public/assets/css/style.css" (format "resources/public/assets/css/style-%s.css" timestamp)]
        js ["resources/public/assets/js/app.js" (format "resources/public/assets/js/app-%s.js" timestamp)]]
    (rename-file (first css) (second css))
    (rename-file (first js) (second js))
    (spit "resources/public/index.html"
          (-> (slurp "resources/public/index.tmpl")
              (str/replace #"\*style\*" (format "style-%s.css" timestamp))
              (str/replace #"\*app\*" (format "app-%s.js" timestamp))))))

(defn patch-dev []
  (spit "resources/public/index.html"
        (-> (slurp "resources/public/index.tmpl")
            (str/replace #"\*style\*" "style.css")
            (str/replace #"\*app\*" "app.js"))))

