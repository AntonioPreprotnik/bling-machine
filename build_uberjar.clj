(ns build-uberjar
  (:require [clojure.tools.build.api :as b]))

(def lib 'pasta/xiana)
(def version (format "1.2.%s" (b/git-count-revs nil)))
(def class-dir "target/classes")
(def basis (b/create-basis {:project "deps.edn"}))
(def uber-file (format "target/%s-%s-standalone.jar" (name lib) version))
(def exclude [#"META-INF\/license\/LICENSE\..*\.txt"])

(defn clean [_]
  (b/delete {:path "target"}))

(defn uber [_]
  (clean nil)
  (b/copy-dir {:src-dirs ["src/backend" "src/frontend" "src/shared" "resources" "config/prod" "config"]
               :target-dir class-dir})
  (b/compile-clj {:basis basis
                  :src-dirs ["src/backend" "src/frontend" "src/shared"]
                  :class-dir class-dir})
  (b/uber {:class-dir class-dir
           :uber-file uber-file
           :basis basis
           :exclude exclude
           :main 'app/core})
  (println (str "Created file: " uber-file)))
