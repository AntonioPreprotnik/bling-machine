(ns user
  (:gen-class)
  (:require [app.core :refer [->system]]
            [app.funicular :as api]
            [clojure.tools.logging :refer [*tx-agent-levels*]]
            [clojure.tools.namespace.repl :refer [refresh set-refresh-dirs]]
            [hawk.core :as hawk]
            [piotr-yuxuan.closeable-map :refer [closeable-map]]
            [shadow.cljs.devtools.api :as shadow.api]
            [shadow.cljs.devtools.server :as shadow.server]
            [state :as st :refer [dev-sys]]))
            ;[tdebug :refer [trace> trace>>]]))
            ;[reveal
            ; :refer
            ; [add-tap-rui snapshot-rui]]))


(alter-var-root #'*tx-agent-levels* conj :debug :trace)

(set-refresh-dirs "dev" "src" "resources")

(defn start-system []
  (reset! dev-sys (->system)))

(defn stop-system []
  (when (:webserver @dev-sys)
    (.close @dev-sys))
  (reset! dev-sys (closeable-map {})))

(defn restart-system
  "Stops system, refreshes changed namespaces in REPL and starts the system again."
  []
  (stop-system)
  (refresh :after 'user/start-system))

(defn- clojure-or-edn-file? [_ {:keys [file]}]
  (re-matches #"[^.].*(\.clj|\.edn)$" (.getName file)))

(defn- system-watch-handler [context _event]
  (binding [*ns* *ns*]
    (restart-system)
    context))

(defn watch-backend
  "Automatically restarts the system if backend related files are changed."
  []
  (hawk/watch! [{:paths   ["src/backend/" "src/shared" "config/dev"]
                 :filter  clojure-or-edn-file?
                 :handler system-watch-handler}]))

(defn watch-frontend
  "Automatically re-builds frontend and re-renders browser page if frontend related files are changed."
  []
  (shadow.server/start!)
  (shadow.api/watch :app))

(defn start-dev
  "Starts development system and runs watcher for auto-restart."
  []
  (watch-backend)
  (watch-frontend)
  (start-system))

(comment
  (start-dev)
  (start-system)
  (stop-system)
  (restart-system)

  (-> @st/dev-sys
      :app/funicular
      (api/execute {:queries {:user [:api.user/get-all {}]}}))

  (-> @st/dev-sys
      :app/funicular
      (api/execute {:command [:api.user/create {:email "test@vbt.com"
                                                :first-name "First"
                                                :last-name "Last"
                                                :zip "10000"}]}))

  (-> @st/dev-sys
      :app/funicular
      (api/execute {:command [:api.user/update {:user-id #uuid"bb621b8b-a841-44c5-b393-01d4411bfb10"
                                                :data    {:email      "test@vbt.com"
                                                          :first-name "First"
                                                          :last-name  "Last"
                                                          :zip        "20000"}}]}))

  (-> @st/dev-sys
      :app/funicular
      (api/execute {:queries {:user [:api.user/get-one {:user-id #uuid"bb621b8b-a841-44c5-b393-01d4411bfb10"}]}})))

