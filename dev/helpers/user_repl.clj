(ns helpers.user-repl
  "REPL namespace used for testing funicular user related API calls."
  (:require
   [app.helpers :refer [command! query!]]
   [state :refer [dev-sys]]))

(def ^:private funicular (:app/funicular @dev-sys))
(def ^:private user-id #uuid "da63492d-d8ab-4166-919b-01d5e48cae78")

(comment
  (query! funicular :api.user/get-all :user {})

  (command! funicular :api.user/create {:email "test@vbt.com"
                                        :first-name "First"
                                        :last-name "Last"
                                        :zip "10000"})

  (command! funicular :api.user/update {:user-id user-id
                                        :data {:first-name "First"
                                               :last-name "Last"
                                               :zip "20000"}})

  (query! funicular :api.user/get-one :user {:user-id user-id}))

