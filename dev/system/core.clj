(ns system.core
  (:require
   [app.backend.core :refer [->system]]
   [clojure.tools.logging :refer [*tx-agent-levels*]]
   [clojure.tools.namespace.repl :refer [refresh refresh-all set-refresh-dirs]]
   [io.aviso.ansi :as ansi]
   [io.aviso.exception :refer [write-exception]]
   [piotr-yuxuan.closeable-map :refer [closeable-map]]
   [system.state :refer [dev-state]]))

(set-refresh-dirs "dev" "src" "resources")

(alter-var-root #'*tx-agent-levels* conj :debug :trace)

(defn- log-title [title]
  (println "")
  (-> (str "[:backend] " title)
      ansi/cyan
      println))

(defn start-system
  "Initializes running closeable map for a system state."
  []
  (log-title "Starting system")
  (try (reset! dev-state (->system))

       (catch Exception e
         (write-exception e))))

(defn stop-system
  "Stops active web server and initializes empty closeable map for a system state."
  []
  (log-title "Stopping system")
  (try (when (:webserver @dev-state) (.close @dev-state))
       (reset! dev-state (closeable-map {}))

       (catch Exception e
         (write-exception e))))

(defn- refresh-partial? [filename]
  (and filename (re-matches #"[^.].*\.(clj|cljc)$" filename)))

(defn restart-system
  "Stops system, refreshes changed namespaces in REPL and starts the system again."
  ([]
   (restart-system nil))

  ([filename]
   (try (stop-system)
        (log-title "Refreshing namespaces")
        (if (refresh-partial? filename)
          (do (refresh :after 'system.core/start-system)
              (load-file filename))
          (refresh-all :after 'system.core/start-system))

        (catch Exception e
          (write-exception e)))))
