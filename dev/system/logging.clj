(ns system.logging
  (:require
   [clojure.java.io]
   [clojure.string :as str]
   [clojure.tools.namespace.find :refer  [find-namespaces-in-dir]]
   [clojure.tools.namespace.repl :refer [disable-reload!]]
   [taoensso.timbre :refer [color-str error info]]))

(disable-reload!)

(defn format-log [log]
  (let [log-rows      (str/split-lines log)
        reloading     (first log-rows)
        error         (= (count log-rows) 2)
        system-log    (if error
                        (last log-rows)
                        (let [log-body (rest log-rows)]
                          (str (first (str/split (first log-body) #"\[m ")) "\n"
                               (str/join "\n" (drop 2 log-rows)))))]
    [reloading system-log error]))

(defn log-wrapper [output title title-color body-color log-type]
  (println (title-color title))
  (let [output-str (with-out-str (output))]
    (case log-type
      :info (info (color-str body-color output-str))
      :error (error (color-str body-color output-str)))))

(defn format-error [err-map]
  (let [[file line message] (->> (:findings err-map)
                                 (filter #(= :error (:level %)))
                                 (map #(select-keys % [:filename :row :message]))
                                 first
                                 (map #(val %)))]
    (when file (str file ":" line " - " message))))

(defn ns-to-file
  [ns]
  (-> (->> (ns-map ns)
           (filter #(.contains (str (val %)) (str ns)))
           (map second)
           (map meta)
           (drop-while #(not (:file %)))
           first
           ((juxt :ns :file)))
      (update-in [0] str)))

(defn- clojure-file? [file]
  (re-matches #"[^.].*(\.clj)$" (.getName file)))

(def clj-files
  (let [src       (clojure.java.io/file "src")
        dev       (clojure.java.io/file "dev")
        src-files (file-seq src)
        dev-files (file-seq dev)
        files     (into src-files dev-files)
        clj-files (filter clojure-file? files)]
    (filter #(not (.contains (str %) "system/logging")) clj-files)))

(def clj-nss
  (let [src (find-namespaces-in-dir (clojure.java.io/file "src"))
        dev (find-namespaces-in-dir (clojure.java.io/file "dev"))]
    (into src dev)))

(defn load-clj-files []
  (->> (mapv #(load-file (str %)) clj-files)))

(declare nss-files)

(declare get-err-ns)

(load-clj-files)

(def nss-files
  (-> (->> (for [clj-ns clj-nss]
             (ns-to-file clj-ns))
           (into {}))
      (update-keys keyword)))

(defn get-err-ns [loading-err]
  (let [err-ns (read-string (last (str/split loading-err #":error-while-loading ")))]
    (vector ((keyword err-ns) nss-files))))
