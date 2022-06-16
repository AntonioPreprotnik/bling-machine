(ns app.backend.funicular
  (:require
   [app.backend.readers :refer [readers]]
   [app.shared.schema :refer [registry]]
   [clojure.edn :as edn]
   [clojure.spec.alpha :as s]
   [com.verybigthings.funicular.core :as funicular]))

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
  (let [funicular-cnf (edn/read-string {:readers readers :config config} (slurp "src/app/shared/funicular.edn"))
        funicular-cnf' (update-cnf funicular-cnf config)
        {:keys [context] :as api} funicular-cnf'
        compiled (funicular/compile api {:malli/registry registry})]
    (-> config
        (assoc :app/funicular
               (reify IFunucilarApi
                 (execute [this request]
                   (execute this request nil))
                 (execute [_ request request-context]
                   (funicular/execute compiled (merge context request-context) request))
                 (inspect [_]
                   (funicular/inspect compiled)))))))
