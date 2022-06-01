(ns app.helpers
  (:require
   [app.funicular :as funicular]
   [tick.alpha.api :as tick]))

(defn ms->secs
  "Convert time from milliseconds to seconds"
  [ms] (quot ms 1000))

(defn current-time-in-secs
  "Get current time in milliseconds and convert to seconds"
  [] (ms->secs (System/currentTimeMillis)))

(defn timestamp
  "Generate future timestamp with provided duration of hours.
  By default, generated timestamp duration is 24 hours."
  ([] (timestamp 24))
  ([hours] (tick/>> (tick/now) (tick/new-duration hours :hours))))

;;------------------------------------------------------------------------------
;; MAPS
;;------------------------------------------------------------------------------

(defn namespace-keys
  "Add namespace to every key of map (top level):
  (namespace-keys :num {:one 1 :two 2}) => #:num{:one 1 :two 2}"
  [ns m] (update-keys m (fn [k] (keyword (name ns) (name k)))))

(defn dissoc-by-values
  "Dissocs all keys for which values are truthy for the predicate function."
  [map vals]
  (->> map
       (remove #(contains? (set vals) (second %)))
       (into {})))

(defn dissoc-by-value-pred
  "Dissocs all keys for which values are truthy for the predicate function."
  [map pred]
  (->> map
       (remove #(pred (second %)))
       (into {})))

(defn update-if-exists
  "Updates map with given function if the key exists."
  [map key fun]
  (if (get map key)
    (update map key fun)
    map))

;;------------------------------------------------------------------------------
;; FUNICULAR
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
