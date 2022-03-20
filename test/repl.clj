(ns repl
  (:require
   [kaocha.repl :as k]))
    ;[tdebug :refer [trace> trace>>]]
    ;[reveal
    ; :refer
    ; [add-tap-rui snapshot-rui]]))

(comment
  (k/run-all)
  (k/run 'backend)
  (k/run 'app.domain.handlers.user-test))
