(ns app.test-repl
  (:require
   [kaocha.repl :as k]))

(comment
  (k/run-all)

  ;;# SHARED
  ;;# --------------------------------------------------------------------------

  (k/run :app/shared)

  ;;# BACKEND
  ;;# --------------------------------------------------------------------------

  (k/run :app/backend)
  (k/run 'app.backend.domain.handlers.admin-test)

  ;;# FRONTEND
  ;;# --------------------------------------------------------------------------

  (k/run :app/frontend))
