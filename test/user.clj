(ns user
  (:require
   [kaocha.repl :as k]))

(comment
  (k/run-all)
  (k/run :shared)
  (k/run :backend)
  (k/run 'backend.app.web.api.handlers.user-test)
  (k/run 'backend.app.penkala/rollback-transaction-check)
  (k/run 'backend.app.penkala))
