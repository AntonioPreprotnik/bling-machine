(ns system.core
  (:require
   [app.backend.core :refer [->system]]
   [clj-kondo.core :as kondo]
   [clojure.string :as str]
   [clojure.tools.logging :refer [*tx-agent-levels*]]
   [clojure.tools.namespace.repl :refer [refresh refresh-all set-refresh-dirs]]
   [io.aviso.ansi :as ansi]
   [piotr-yuxuan.closeable-map :refer [closeable-map]]
   [system.state :refer [dev-sys]]
   [taoensso.timbre :refer [color-str error info]]))

(set-refresh-dirs "dev" "src" "resources")

(alter-var-root #'*tx-agent-levels* conj :debug :trace)

(declare restart-system)

(defn format-log [log]
  (let [log-rows      (str/split-lines log)
        reloading     (first log-rows)
        error         (= (count log-rows) 2)
        system-log    (if error
                        (last log-rows)
                        (str/replace-first (str/join "\n" (-> (update-in
                                                               (into [] (rest log-rows)) [1]
                                                               #(last (str/split % #" - ")))
                                                              (update-in [2]
                                                                         #(str/join " " (rest (str/split % #" - "))))))
                                           #"\n" " "))]
    [reloading system-log error]))

(defn start-system []
  (println "")
  (info  (color-str :cyan "Start system:"))
  (let [stop-str (with-out-str (reset! dev-sys (->system)))]
    (info (color-str :green stop-str))))

(defn stop-system []
  (println "")
  (info (color-str :cyan "Stop system:"))
  (let [stop-str (with-out-str (do (when (:webserver @dev-sys)
                                     (.close @dev-sys))
                                   (reset! dev-sys (closeable-map {}))))]
    (info (color-str :blue stop-str))))

(defn restart-system
  "Stops system, refreshes changed namespaces in REPL and starts the system again."
  [all]
  (println "")
  (print  (ansi/bold-cyan "Restart- system:"))
  (stop-system)
  (let [[reloading system-log error?]
        (format-log (with-out-str (if all
                                    (refresh-all :after 'system.core/start-system)
                                    (refresh :after 'system.core/start-system))))]
    (info (color-str :cyan "Reloading system:"))
    (info (color-str :yellow reloading))
    (println "")
    (if error? (let [err (-> (kondo/run! {:lint ["src/app/backend" "src/app/shared"]})
                             :findings)]
                 (error (color-str :red system-log))
                 (error (color-str :purple err)))
        (info (color-str :green system-log)))
    (print (if error? (ansi/bold-red "System NOT restarted.")
               (ansi/bold-cyan "System restarted.")))
    (println "")))


