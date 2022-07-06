(ns system.watchers
  "Collection of system change watchers for backend, frontend and postcss.
  Every watcher detects changes in their corresponding namespaces and reflect
  changes by restarting/rerendering changed parts on the system."
  (:require
   [clojure.core.async :refer [go]]
   [hawk.core :as hawk]
   [io.aviso.ansi :as ansi]
   [shadow.cljs.devtools.api :as shadow.api]
   [system.state :refer [backend-watcher postcss-watcher]])
  (:import
   [java.util Timer TimerTask]))

;;# BACKEND WATCHER
;;# --------------------------------------------------------------------------

(defn- debounce [callback timeout]
  (let [timer (Timer.)
        task (atom nil)]
    (fn [& args]
      (when-let [running-task ^TimerTask @task]
        (.cancel running-task))

      (let [new-task (proxy [TimerTask] []
                       (run []
                         (apply callback args)
                         (reset! task nil)
                         (.purge timer)))]
        (reset! task new-task)
        (.schedule timer new-task timeout))
      (first args))))

(defn- clojure-file? [_ {:keys [file]}]
  (re-matches #"[^.].*(\.clj|\.edn|\.cljc)$" (.getName file)))

(defn- watch-handler [context event]
  (binding [*ns* *ns*]
    ((:fn context) (.getPath (:file event)))
    context))

(defn watch-backend
  "Automatically restarts the system if backend related files are changed."
  [callback]
  (let [watcher (hawk/watch! {:watcher :polling}
                             [{:paths ["src/app/backend" "src/app/shared" "config/dev"]
                               :context (constantly {:fn callback})
                               :filter clojure-file?
                               :handler (debounce watch-handler 500)}])]
    (reset! backend-watcher watcher)))

(defn stop-backend-watcher []
  (hawk/stop! backend-watcher)
  (reset! backend-watcher nil))

;;# FRONTEND WATCHER
;;# --------------------------------------------------------------------------

(defn watch-frontend
  "Automatically re-builds frontend and re-renders browser page if frontend
  related files are changed."
  []
  (shadow.api/watch :app))

;;# POSTCSS WATCHER
;;# --------------------------------------------------------------------------

(defn postcss-watch
  "Runs postcss watcher in parallel thread and redirects std output to main console."
  []
  (println (ansi/cyan "Starting postcss watcher"))
  (let [watcher (-> (ProcessBuilder. ["npm" "run" "postcss:watch"])
                    .inheritIO
                    .start)]
    (reset! postcss-watcher watcher)
    (.waitFor watcher)))

(defn reset-postcss-watch
  "Kills current postcss process and start the new one."
  []
  (when @postcss-watcher (.destroyForcibly @postcss-watcher))
  (go (postcss-watch)))
