(ns build-uberjar
  (:require
   [clojure.tools.build.api :as b]))

(def lib 'bling-machine-api)
(def class-dir "target/classes")
(def basis (b/create-basis {:project "deps.edn"}))
(def uber-file (format "target/%s-standalone.jar" (name lib)))
(def exclude [#"META-INF\/license\/LICENSE\..*\.txt"])

(defn clean [_]
  (b/delete {:path "target"}))

(defn uber [_]
  (clean nil)
  (b/copy-dir {:src-dirs ["src" "resources" "config/prod" "config"]
               :target-dir class-dir})
  (b/compile-clj {:basis basis
                  :src-dirs ["src"]
                  :class-dir class-dir})
  (b/uber {:class-dir class-dir
           :uber-file uber-file
           :basis basis
           :exclude exclude
           :main 'app.backend/core})
  (println (str "Created file: " uber-file)))
