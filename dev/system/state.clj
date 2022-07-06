(ns system.state
  (:require
   [clojure.tools.namespace.repl :refer [disable-reload!]]
   [piotr-yuxuan.closeable-map :refer [closeable-map]]))

(disable-reload!)

(defonce dev-system (atom (closeable-map {})))
(defonce postcss-watcher (atom nil))
(defonce backend-watcher (atom nil))