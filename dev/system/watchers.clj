(ns system.watchers
  "Collection of system change watchers for backend, frontend and postcss.
  Every watcher detects changes in their corresponding namespaces and reflect
  changes by restarting/rerendering changed parts on the system."
  (:require
   [clojure.core.async :refer [go]]
   [hawk.core :as hawk]
   [io.aviso.ansi :as ansi]
   [shadow.cljs.devtools.api :as shadow.api]
   [shadow.cljs.devtools.server :as shadow.server])
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
  (hawk/watch! [{:paths ["src/app/backend/" "src/app/shared" "config/dev"]
                 :context (constantly {:fn callback})
                 :filter clojure-file?
                 :handler (debounce watch-handler 500)}]))

;;# FRONTEND WATCHER
;;# --------------------------------------------------------------------------

(defn watch-frontend
  "Automatically re-builds frontend and re-renders browser page if frontend
  related files are changed."
  []
  (shadow.server/start!)
  (shadow.api/watch :app))

;;# POSTCSS WATCHER
;;# --------------------------------------------------------------------------

(def ^:private postcss-watcher-proc
  "Atom that tracks current postcss watcher process. Used for killing running postcss
  process and staring a new one."
  (atom nil))

(defn postcss-watch
  "Runs postcss watcher in parallel thread and redirects std output to main console."
  []
  (println (ansi/cyan "Starting postcss watcher"))
  (let [proc (-> (ProcessBuilder. ["npm" "run" "postcss:watch"])
                 .inheritIO
                 .start)]
    (reset! postcss-watcher-proc proc)
    (.waitFor proc)))

(defn reset-postcss-watch
  "Kills current postcss process and start the new one."
  []
  (when @postcss-watcher-proc (.destroyForcibly @postcss-watcher-proc))
  (go (postcss-watch)))
