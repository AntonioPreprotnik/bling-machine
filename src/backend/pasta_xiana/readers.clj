(ns pasta-xiana.readers
  (:require [state :as st]))



(defn resolve-var [sym]
  (let [resolved (clojure.core/requiring-resolve sym)]
    (if resolved
      (var-get resolved)
      (throw (ex-info (str sym " can't be resolved") {:var sym})))))


(defn get-config-key [key]
  (name key))

(def readers
  {'resolve resolve-var
   'config-key get-config-key})
