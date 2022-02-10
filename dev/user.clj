(ns user
  (:gen-class)
  (:require
   [app.core :refer [->system app-cfg]]
   [clojure.tools.logging :refer [*tx-agent-levels*]]
   [clojure.tools.namespace.repl :refer [refresh-all refresh]]
   [piotr-yuxuan.closeable-map :refer [closeable-map]]
   [shadow.cljs.devtools.api :as shadow.api]
   [shadow.cljs.devtools.server :as shadow.server]
   [state :as st :refer [dev-sys]]
   [app.funicular :as api]
   [reveal
    :refer
    [add-tap-rui open-snapshot]
    :as rui]))

(alter-var-root #'*tx-agent-levels* conj :debug :trace)

(def dev-app-config
  app-cfg)


(defn start-dev-system
  []
  (shadow.server/start!)
  (shadow.api/watch :app)
  (reset! dev-sys (->system dev-app-config)))

(defn stop-dev-system
  []
  (when (:webserver @dev-sys)
    (.close @dev-sys)
    (reset! dev-sys (closeable-map {}))))


(defn reset-dev-system
  []
  (stop-dev-system)
  (refresh-all :after `user/start-dev-system))


(comment
  (start-dev-system)

  (-> @st/dev-sys
      :app/funicular
      (api/execute {:command [:api.user/create {:email "atd@vbt.com"
                                                :first-name "Frka1"
                                                :last-name "Trle1"
                                                :zip "10000"}]}))
  (-> @st/dev-sys
      :app/funicular
      (api/execute {:queries {:user [:api.user/get-one {:user-id #uuid"286696ac-5977-4b3e-8392-5ec4a8e3784e"}]}}))

  (-> @st/dev-sys
        :app/funicular
        (api/execute {:queries {:user [:api.user/get-all {}]}}))

  (-> @st/dev-sys
      :app/funicular
      (api/execute {:command [:api.user/create {:email "adrrgg@vbt.com"
                                                :first-name "Frka12"
                                                :last-name "Trle12"
                                                :zip "10000"}]}))

  (-> @st/dev-sys
        :app/funicular
        (api/execute {:command [:api.user/update {:user-id #uuid"bb621b8b-a841-44c5-b393-01d4411bfb10"
                                                  :data    {:email      "ad@vbt.com"
                                                            :first-name "Frka21"
                                                            :last-name  "Trle21"
                                                            :zip        "10000"}}]}))

  (-> @st/dev-sys
        :app/funicular
        (api/execute {:queries {:user [:api.user/get-one {:user-id #uuid"bb621b8b-a841-44c5-b393-01d4411bfb10"}]}})))
