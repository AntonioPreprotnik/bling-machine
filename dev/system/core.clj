(ns system.core
  (:require
   [app.backend.core :refer [->system]]
   [clj-kondo.core :as kondo]
   clojure.pprint
   [clojure.string :as str]
   [clojure.tools.logging :refer [*tx-agent-levels*]]
   [clojure.tools.namespace.repl :refer [refresh refresh-all set-refresh-dirs]]
   [io.aviso.ansi :as ansi]
   [piotr-yuxuan.closeable-map :refer [closeable-map]]
   [system.logging :refer [format-error format-log get-err-ns log-wrapper]]
   [system.state :refer [dev-sys]]
   [taoensso.timbre :refer [color-str error]]))

(set-refresh-dirs  "src" "resources")

(alter-var-root #'*tx-agent-levels* conj :debug :trace)

(declare restart-system)

(defn start-system []
  (-> #(reset! dev-sys (->system))
      (log-wrapper "Starting system" ansi/cyan :green :info)))

(defn stop-system []
  (-> #(do (when (:webserver @dev-sys)
             (.close @dev-sys))
           (reset! dev-sys (closeable-map {})))
      (log-wrapper "Stopping system" ansi/cyan :blue :info)))

(defn restart-system
  "Stops system, refreshes changed namespaces in REPL and starts the system again."
  [all]
  (println "")
  (stop-system)
  (let [format-error-   format-error
        get-err-ns- get-err-ns
        [reloading system-log error?]
        (format-log (with-out-str (if all
                                    (refresh-all :after 'system.core/start-system)
                                    (refresh :after 'system.core/start-system))))
        reload-sys      #(print reloading)]
    (log-wrapper  reload-sys "Reloading system" ansi/cyan :yellow :info)
    (println "")
    (if error? (let [err (with-out-str
                           (print (-> (kondo/run! {:lint (get-err-ns- system-log)})
                                      (format-error-))))]
                 (println "")
                 (error (color-str :red system-log))
                 (if (= err "nil")
                   (do (error (color-str :red (str "Run time error!\n Switch REPL to " (last (str/split system-log #":error-while-loading"))
                                                   " namespace and load it in REPL. Inspect stack trace with (clojure.repl/pst)")))
                       (load-file (first (get-err-ns- system-log))))
                   (error (color-str :red err))))
        (print system-log)))
  (println ""))



