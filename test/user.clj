(ns user
  (:require
   [kaocha.repl :as k]))

(comment
  (k/run-all)
  (k/run 'backend.app.web.api.handlers.user-test)
  (k/run 'backend.app.web.api.handlers.user-test/create-and-get))
