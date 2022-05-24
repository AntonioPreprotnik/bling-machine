(ns frontend-version-patcher
  (:require
   [clojure.java.io :as io]
   [clojure.string :as str]))

(defn rename-file [old new]
  (.renameTo (io/file old) (io/file new)))

(defn delete-directory-recursive [^java.io.File file]
  "Recursively delete a directory."
  (when (.isDirectory file)
    (run! delete-directory-recursive (.listFiles file)))
  (io/delete-file file))

(defn clear-resources []
  (let [css-root (io/file "resources/public/assets/css")
        js-root  (io/file "resources/public/assets/js")
        assets-roots [js-root css-root]]
    (doseq [file assets-roots]
      (when (.exists file) (delete-directory-recursive file)))))

(defn patch-prod []
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

