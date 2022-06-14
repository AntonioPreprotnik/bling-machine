(ns test-repl
  (:require
   [kaocha.repl :as k]))

(comment
  (k/run-all)
  (k/run :shared)
  (k/run :backend)
  (k/run 'backend.web.api.handlers.user-test)
  (k/run 'backend.penkala-test/rollback-transaction-check)
  (k/run 'backend.penkala-test))
