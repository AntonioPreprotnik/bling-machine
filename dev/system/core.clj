(ns system.core
  (:require
   [app.backend.core :refer [->system]]
   [clojure.tools.namespace.repl :refer [refresh refresh-all set-refresh-dirs]]
   [io.aviso.ansi :as ansi]
   [io.aviso.exception :refer [write-exception]]
   [piotr-yuxuan.closeable-map :refer [closeable-map]]
   [system.state :refer [dev-system]]))

(set-refresh-dirs "dev" "src" "resources")

(defn- log-title [title]
  (println "")
  (-> (str "[:app] " title)
      ansi/cyan
      println))

(defn start-system
  "Initializes running closeable map for a system state."
  []
  (log-title "Starting system")
  (try (reset! dev-system (->system))
       (catch Exception e
         (write-exception e))))

(defn stop-system
  "Stops active web server and initializes empty closeable map for a system state."
  []
  (log-title "Stopping system")
  (try (when (:webserver @dev-system) (.close @dev-system))
       (reset! dev-system (closeable-map {}))

       (catch Exception e
         (write-exception e))))

(defn- code-file? [filename]
  (and filename (re-matches #"[^.].*\.(clj|cljc)$" filename)))

(defn- refresh-namespaces [filename]
  (let [refresh-result (if (code-file? filename)
                         (refresh :after 'system.core/start-system)
                         (refresh-all :after 'system.core/start-system))]
    (when (ex-message refresh-result)
      (throw refresh-result))))

(defn restart-system
  "Stops system, refreshes changed namespaces in REPL and starts the system again."
  ([]
   (restart-system nil))

  ([filename]
   (try (stop-system)
        (log-title "Reloading namespaces")
        (refresh-namespaces filename)

        (catch Exception e
          (write-exception e)))))
