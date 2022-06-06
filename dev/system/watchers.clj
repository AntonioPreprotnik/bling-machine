(ns system.watchers
  (:require
   [clojure.core.async :refer  [go]]
   [hawk.core :as hawk]
   [shadow.cljs.devtools.api :as shadow.api]
   [shadow.cljs.devtools.server :as shadow.server]))

(import '[java.util Timer TimerTask])

(defn debounce
  ([f reset-fn] (debounce f reset-fn 50))
  ([f reset-fn timeout]
   (let [timer (Timer.)
         task (atom nil)]
     (fn [& args]
       (when-let [t ^TimerTask @task]
         (.cancel t))
       (let [new-task (proxy [TimerTask] []
                        (run []
                          (apply f args)
                          (reset! task nil)
                          (.purge timer)))]
         (reset! task new-task)
         (.schedule timer new-task timeout))
       reset-fn))))

(defn- clojure-or-edn-file? [_ {:keys [file]}]
  (re-matches #"[^.].*(\.clj|\.cljc|\.edn)$" (.getName file)))

(defn- system-watch-handler [reset-fn _event]
  (binding [*ns* *ns*]
    (reset-fn)))

(defn watch-backend
  "Automatically restarts the system if backend related files are changed."
  [fn]
  (hawk/watch! [{:paths   ["src/backend/" "src/shared" "config/dev"]
                 :context (constantly fn)
                 :filter  clojure-or-edn-file?
                 :handler (debounce #(system-watch-handler %1 %2) fn)}]))

(defn watch-frontend
  "Automatically re-builds frontend and re-renders browser page if frontend related files are changed."
  ([]
   (watch-frontend :app))
  ([build-id]
   (shadow.server/start!)
   (shadow.api/watch build-id)))

(def css-watcher-proc (atom nil))

(defn postcss-watch []
  (let [proc (-> (ProcessBuilder. ["npm" "run" "postcss:watch"]) .inheritIO .start)]
    (reset! css-watcher-proc proc)
    (.waitFor proc)))

(defn reset-postcss-watch
  "Run this function from REPL to kill the stuck postcss process and start the new one"
  []
  (when @css-watcher-proc (.destroyForcibly @css-watcher-proc))
  (go (postcss-watch)))
