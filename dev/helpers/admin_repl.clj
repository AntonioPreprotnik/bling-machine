(ns helpers.admin-repl
  "REPL namespace used for testing funicular user related API calls."
  (:require
   [app.backend.helpers :refer [command! query!]]
   [system.state :refer [dev-system]]))

(defn ^:private funicular []
  (:app/funicular @dev-system))

(def ^:private user-id #uuid "33a5ece0-8d09-452b-8cd8-8918b6e55fad")

(comment
  (query! (funicular) :api.admin/get-users :user {})

  (command! (funicular) :api.admin/create-user {:email "test@email.com"
                                                :first-name "First"
                                                :last-name "Last"
                                                :password "password"
                                                :is-admin true})

  (command! (funicular) :api.admin/update-user {:user-id user-id
                                                :data {:first-name "First"
                                                       :last-name "Last"}})

  (command! (funicular) :api.admin/delete-user {:user-id user-id})

  (query! (funicular) :api.admin/get-user :user {:user-id user-id}))

