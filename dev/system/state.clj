(ns system.state
  (:require
   [clojure.tools.namespace.repl :refer [disable-reload!]]
   [piotr-yuxuan.closeable-map :refer [closeable-map]]))

(disable-reload!)

(defonce dev-state (atom (closeable-map {})))
