(ns system.core
  (:require
   [app.core-be :refer [->system]]
   [clojure.tools.logging :refer [*tx-agent-levels*]]
   [clojure.tools.namespace.repl :refer [refresh set-refresh-dirs]]
   [piotr-yuxuan.closeable-map :refer [closeable-map]]
   [system.state :refer [dev-sys]]))

(set-refresh-dirs "dev" "src" "resources")

(alter-var-root #'*tx-agent-levels* conj :debug :trace)

(declare restart-system)

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
  (refresh :after 'system.core/start-system))


