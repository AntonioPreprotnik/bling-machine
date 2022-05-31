(ns user
  (:require
    [shadow.cljs.devtools.api :as shadow.api]))

(defn release-frontend [{:keys [build]}]
  (shadow.api/release build))
