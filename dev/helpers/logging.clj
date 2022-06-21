(ns helpers.logging
  (:require
   [clojure.string :as str]
   [taoensso.timbre :refer [color-str error info]]))

(defn format-log [log]
  (let [log-rows      (str/split-lines log)
        reloading     (first log-rows)
        error         (= (count log-rows) 2)
        system-log    (if error
                        (last log-rows)
                        (let [log-body (rest log-rows)]
                          (str (first (str/split (first log-body) #"\[m ")) "\n"
                               (str/join "\n" (drop 2 log-rows)))))]
    (tap> log-rows)
    [reloading system-log error]))

(defn log-wrapper [output title title-color body-color log-type]
  (println (title-color title))
  (let [output-str (with-out-str (output))]
    (case log-type
      :info (info (color-str body-color output-str))
      :error (error (color-str body-color output-str)))))
