(ns app.test-repl
  (:require
   [kaocha.repl :as k]))

(comment
  (k/run-all)
  (k/run :shared)
  (k/run :backend)
  (k/run 'app.backend.web.api.handlers.user-test)
  (k/run 'app.backend.penkala-test/rollback-transaction-check)
  (k/run 'app.backend.penkala-test))
