(ns app.funicular
  (:require [clojure.spec.alpha :as s]
            [com.verybigthings.funicular.core :as f]
            [schema :refer [registry]]
            [app.readers :refer [readers]]
            [clojure.edn :as edn]))
            ;[duct.logger :refer [log]]))

(s/check-asserts true)

(defprotocol IFunucilarApi
  (execute [this request] [this request request-context])
  (inspect [this]))

(defn update-cnf [funicular-cnf config]
  (update-in funicular-cnf [:context]
    #(reduce-kv (fn [m k v] (merge m (if (= (namespace v) "config")
                                       {k ((keyword (name v)) config)}
                                       {k v})))
       {} %)))

(defn init [config]
  (let [funicular-cnf (edn/read-string {:readers readers :config config} (slurp "config/dev/funicular.edn"))
        funicular-cnf' (update-cnf funicular-cnf config)
        {:keys [context] :as api} funicular-cnf'
        compiled (f/compile api {:malli/registry registry})]
    (-> config
        (assoc :app/funicular
               (reify IFunucilarApi
                 (execute [this request]
                   (execute this request nil))
                 (execute [_ request request-context]
                   #_(log logger :info :funicular/request request)
                   (f/execute compiled (merge context request-context) request))
                 (inspect [_]
                   (f/inspect compiled)))))))
