(ns app.logging
  (:require [taoensso.timbre :as log]
            taoensso.timbre.tools.logging))

(defn init! [{log :logging/timbre :as config}]
  (log/merge-config! log)
  (taoensso.timbre.tools.logging/use-timbre)
  config)

(comment
  ;; to see current config check out `log/*config*` variable
  ;; to set different level use
  (log/set-level! :error))
