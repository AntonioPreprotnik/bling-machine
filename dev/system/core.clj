(ns system.core
  (:require
   [app.backend.core :refer [->system]]
   [clj-kondo.core :as kondo]
   clojure.pprint
   [clojure.string :as str]
   [clojure.tools.logging :refer [*tx-agent-levels*]]
   [clojure.tools.namespace.repl :refer [refresh refresh-all set-refresh-dirs]]
   [helpers.logging :refer [format-log log-wrapper]]
   [io.aviso.ansi :as ansi]
   [piotr-yuxuan.closeable-map :refer [closeable-map]]
   [system.state :refer [dev-sys]]
   [taoensso.timbre :refer [color-str error]]))

(set-refresh-dirs "dev" "src" "resources")

(alter-var-root #'*tx-agent-levels* conj :debug :trace)

(declare restart-system)

(defn start-system []
  (-> #(reset! dev-sys (->system))
      (log-wrapper "Start system" ansi/cyan :green :info)))

(defn stop-system []
  (-> #(do (when (:webserver @dev-sys)
             (.close @dev-sys))
           (reset! dev-sys (closeable-map {})))
      (log-wrapper "Stop system" ansi/cyan :blue :info)))

(defn restart-system
  "Stops system, refreshes changed namespaces in REPL and starts the system again."
  [all]
  (println  (ansi/bold-cyan "\nRestart system:"))
  (stop-system)
  (let [[reloading system-log error?]
        (format-log (with-out-str (if all
                                    (refresh-all :after 'system.core/start-system)
                                    (refresh :after 'system.core/start-system))))
        reload-sys #(print reloading)]
    (log-wrapper  reload-sys "Reloading system" ansi/cyan :yellow :info)
    (println "")
    (if error? (let [err (with-out-str
                           (clojure.pprint/print-table (-> (kondo/run! {:lint ["src/app/backend" "src/app/shared"]})
                                                           :findings)))]
                 (error (color-str :red system-log))
                 (if (empty? err)
                   (error (color-str :purple (str "Run time error!\n Switch REPL to " (last (str/split system-log #":error-while-loading"))
                                                  " namespace and load it in REPL. Inspect stack trace with (clojure.repl/pst)")))
                   (error (color-str :purple err))))
        (print system-log))
    (println (if error? (ansi/bold-red "System NOT restarted.")
                 (ansi/bold-cyan "System restarted.")))))


