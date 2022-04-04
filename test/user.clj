(ns user
  (:require
   [kaocha.repl :as k]))

(comment
  (k/run-all)
  (k/run 'backend)
  (k/run 'app.domain.handlers.user-test))
