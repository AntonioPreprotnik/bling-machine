(ns user
  (:gen-class)
  (:require
   [app.core :refer [->system app-cfg]]
   [clojure.tools.logging :refer [*tx-agent-levels*]]
   [clojure.tools.namespace.repl :refer [refresh-all]]
   [piotr-yuxuan.closeable-map :refer [closeable-map]]
   [shadow.cljs.devtools.api :as shadow.api]
   [shadow.cljs.devtools.server :as shadow.server]
   [state :as st :refer [dev-sys]]
   [app.funicular :as api]))

(alter-var-root #'*tx-agent-levels* conj :debug :trace)

(def dev-app-config
  app-cfg)

(defn- stop-dev-system
  []
  (when (:webserver @dev-sys)
    (.close @dev-sys)
    (refresh-all)
    (reset! dev-sys (closeable-map {}))))

(defn start-dev-system
  []
  (stop-dev-system)
  (shadow.server/start!)
  (shadow.api/watch :app)
  (reset! dev-sys (->system dev-app-config)))

(comment
  (start-dev-system)
  (-> @st/dev-sys
      :app/funicular
      (api/execute {:command [:api.user/create {:email "ad@vbt.com"
                                                :first-name "Frka1"
                                                :last-name "Trle1"
                                                :zip "10000"}]}))
  (-> @st/dev-sys
      :app/funicular
      (api/execute {:queries {:user [:api.user/get-one {:user-id #uuid"286696ac-5977-4b3e-8392-5ec4a8e3784e"}]}})))
