(ns app.backend.pipes)

(defn merge-command-response [{:keys [command] :as request}]
  (update request :data merge (:response command)))
