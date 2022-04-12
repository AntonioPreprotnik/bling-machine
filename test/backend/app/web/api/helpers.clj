(ns backend.app.web.api.helpers
  (:require [app.funicular :as funicular]))

(defn get-command [{[_ command-payload] :command}]
  command-payload)

(defn get-query [response query-alias]
  (let [[_ query-payload] (get-in response [:queries query-alias])]
    query-payload))

(defn command! [funicular command payload]
  (-> (funicular/execute funicular {:command [command payload]})
      get-command))

(defn query! [funicular query query-alias payload]
  (let [res (funicular/execute funicular {:queries {query-alias [query payload]}})]
    (get-query res query-alias)))
