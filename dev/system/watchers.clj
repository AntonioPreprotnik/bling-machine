(ns system.watchers
  (:require
   [clojure.core.async :refer  [go]]
   [hawk.core :as hawk]
   [io.aviso.ansi :as ansi]
   [shadow.cljs.devtools.api :as shadow.api]
   [shadow.cljs.devtools.server :as shadow.server]
   [taoensso.timbre :refer  [color-str error]]))

(import '[java.util Timer TimerTask])

(defn debounce
  [f timeout]
  (let [timer (Timer.)
        task (atom nil)]
    (fn [& args]
      (when-let [t ^TimerTask @task]
        (.cancel t))
      (let [new-task (proxy [TimerTask] []
                       (run []
                         (try (apply f args)
                              (catch Exception e
                                (error (color-str :red
                                                  (str "exception in: " (.getName (:file (second args)))
                                                       " error: " (.getMessage e))))))
                         (reset! task nil)
                         (.purge timer)))]
        (reset! task new-task)
        (.schedule timer new-task timeout))
      (first args))))

(defn- clojure-file? [_ {:keys [file]}]
  (re-matches #"[^.].*(\.clj|\.edn)$" (.getName file)))

(defn- edn-file? [{:keys [file]}]
  (re-matches #"[^.].*(\.edn)$" (.getName file)))

(defn- system-watch-handler [ctx e]
  (let [all (edn-file? e)]
    (binding [*ns* *ns*]
      ((:fn ctx) all)
      ctx)))

(defn watch-backend
  "Automatically restarts the system if backend related files are changed."
  [reset-fn]
  (hawk/watch! [{:paths   ["src/app/backend/" "src/app/shared" "config/dev"]
                 :context (constantly  {:fn  reset-fn})
                 :filter  clojure-file?
                 :handler (debounce #(system-watch-handler %1 %2) 500)}]))

(defn watch-frontend
  "Automatically re-builds frontend and re-renders browser page if frontend related files are changed."
  ([]
   (watch-frontend :app))
  ([build-id]
   (shadow.server/start!)
   (shadow.api/watch build-id)))

(def css-watcher-proc (atom nil))

(defn postcss-watch []
  (println (ansi/cyan "Start postcss:watch"))
  (let [proc (-> (ProcessBuilder. ["npm" "run" "postcss:watch"]) .inheritIO .start)]
    (reset! css-watcher-proc proc)
    (.waitFor proc)))

(defn reset-postcss-watch
  "Run this function from REPL to kill the stuck postcss process and start the new one"
  []
  (when @css-watcher-proc (.destroyForcibly @css-watcher-proc))
  (go (postcss-watch)))
