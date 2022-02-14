(ns repl
  (:require
   [kaocha.repl :as k]
   [tdebug :refer [trace> trace>>]]
   [reveal
    :refer
    [add-tap-rui snapshot-rui]]))

(comment
  (add-tap-rui)
  (snapshot-rui nil nil)
  (trace> :dummy "dummy")
  (trace>> :dummy "dummy")
  (k/run-all)
  (k/run 'integration)
  (k/run 'app.integration.pasta-xiana-test))



