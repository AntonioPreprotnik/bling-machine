(ns system
  (:require
   [app.core :refer [->system]]
   [clojure.core.async :refer  [go]]
   [clojure.tools.logging :refer [*tx-agent-levels*]]
   [clojure.tools.namespace.repl :refer [refresh set-refresh-dirs]]
   [hawk.core :as hawk]
   [piotr-yuxuan.closeable-map :refer [closeable-map]]
   [shadow.cljs.devtools.api :as shadow.api]
   [shadow.cljs.devtools.server :as shadow.server]
   [state :refer [dev-sys]]))

(alter-var-root #'*tx-agent-levels* conj :debug :trace)

(declare restart-system)

;;# ----------------------------------------------------------------------------
;;# WATCHERS
;;# ----------------------------------------------------------------------------

(set-refresh-dirs "dev" "src" "resources")

(defn- clojure-or-edn-file? [_ {:keys [file]}]
  (re-matches #"[^.].*(\.clj|\.cljc|\.edn)$" (.getName file)))

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
  ([]
   (watch-frontend :app))
  ([build-id]
   (shadow.server/start!)
   (shadow.api/watch build-id)))

(def css-watch (atom nil))

(defn watch-postcss
  "a"
  []
  (let [proc (-> (ProcessBuilder. ["npm" "run" "postcss:watch"]) .inheritIO .start)]
    (reset! css-watch proc)
    (.waitFor proc)))

(defn reset-postcss-watch
  "Run this function from REPL to kill the stuck postcss process and start the new one"
  []
  (when @css-watch (.destroyForcibly @css-watch))
  (go (watch-postcss)))

;;# ----------------------------------------------------------------------------
;;# DEV SYSTEM
;;# ----------------------------------------------------------------------------

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
  (refresh :after 'system/start-system))

(defn start-dev
  "Starts development system and runs watcher for auto-restart."
  [& _]
  (watch-backend)
  (watch-frontend)
  (start-system)
  (go (watch-postcss)))

(comment
  (start-dev)
  (start-system)
  (stop-system)
  (restart-system))
