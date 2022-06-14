(ns backend.helpers
  (:require
   [backend.funicular :as funicular]))

;;------------------------------------------------------------------------------
;; FUNICULAR HELPERS
;;------------------------------------------------------------------------------

(defn command!
  "Funicular command which returns destructured response payload."
  [funicular command payload]
  (-> funicular
      (funicular/execute {:command [command payload]})
      (get-in [:command 1])))

(defn query!
  "Funicular query which returns destructured response payload."
  [funicular query query-alias payload]
  (-> funicular
      (funicular/execute {:queries {query-alias [query payload]}})
      (get-in [:queries query-alias 1])))
