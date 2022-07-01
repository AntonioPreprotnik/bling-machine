(ns helpers.user-repl
  "REPL namespace used for testing funicular user related API calls."
  (:require
   [app.backend.helpers :refer [command! query!]]
   [system.state :refer [dev-sys]]))

(defn ^:private funicular []
  (:app/funicular @dev-sys))

(def ^:private user-id #uuid "33a5ece0-8d09-452b-8cd8-8918b6e55fad")

(comment
  (query! (funicular) :api.user/get-all :user {})

  (command! (funicular) :api.user/create {:email "test@vbt.com"
                                          :first-name "First"
                                          :last-name "Last"})

  (command! (funicular) :api.user/update {:user-id user-id
                                          :data {:first-name "First"
                                                 :last-name "Last"}})

  (query! (funicular) :api.user/get-one :user {:user-id user-id}))

