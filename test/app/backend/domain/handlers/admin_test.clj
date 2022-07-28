(ns app.backend.domain.handlers.admin-test
  (:require
   [app.backend.domain.handlers.admin :as admin]
   [app.backend.helpers :refer [command!]]
   [app.test-core :refer [get-system]]
   [app.test-fixtures :refer [clean-db]]
   [app.test-helpers :refer [get-unique-integer]]
   [clojure.test :refer [use-fixtures]]
   [state-flow.api :as flow :refer [flow]]
   [state-flow.assertions.matcher-combinators :refer [match?]]
   [state-flow.cljtest :refer [defflow]]))

(use-fixtures :each clean-db)

(defn init []
  (let [system (get-system)]
    {:system system}))

;;# MOCKS
;;# --------------------------------------------------------------------------

(defn log-in [email password]
  (flow "Login admin"
    (flow/swap-state
     (fn [{{funicular :app/funicular} :system :as state}]
       (let [input {:email email :password password}
             response (command! funicular :api.session/login input)]
         (assoc state :jwt (:jwt response)))))))

(defn create-admin
  ([] (create-admin {}))
  ([params]
   (flow "Login user"
     (flow/swap-state
      (fn [{system :system :as state}]
        (let [unique-integer (get-unique-integer)
              data (merge
                    {:email (format "admin-%s@email.com" unique-integer)
                     :first-name "Admin"
                     :last-name (str unique-integer)
                     :password "password"}
                    params
                    {:is-admin true})
              context (assoc system :data data)
              user (admin/create-user context)]
          (assoc state :new-admin user))))
     (flow/get-state :new-admin))))

(defn create-user
  ([] (create-user {}))
  ([params]
   (flow "Create user"
     (flow/swap-state
      (fn [{:keys [jwt] {funicular :app/funicular} :system :as state}]
        (let [unique-integer (get-unique-integer)
              input (merge {:email (format "user-%s@email.com" unique-integer)
                            :first-name "User"
                            :last-name (str unique-integer)
                            :password "password"
                            :app/jwt jwt}
                           params)
              result (command! funicular :api.admin/create-user input)]
          (assoc state :new-user result))))
     (flow/get-state :new-user))))

(defn update-user [params]
  (flow "Update user"
    (flow/swap-state
     (fn [{:keys [jwt] {funicular :app/funicular} :system :as state}]
       (let [input (assoc params :app/jwt jwt)
             result (command! funicular :api.admin/update-user input)]
         (assoc state :updated-user result))))
    (flow/get-state :updated-user)))

(defn get-user [user-id]
  (flow "Gets user by ID"
    (flow/swap-state
     (fn [{:keys [jwt] {funicular :app/funicular} :system :as state}]
       (let [input {:user-id user-id :app/jwt jwt}
             result (command! funicular :api.admin/get-user input)]
         (assoc state :user result))))
    (flow/get-state :user)))

(defn get-users []
  (flow "Gets all users"
    (flow/swap-state
     (fn [{:keys [jwt] {funicular :app/funicular} :system :as state}]
       (let [input {:app/jwt jwt}
             result (command! funicular :api.admin/get-users input)]
         (assoc state :users result))))
    (flow/get-state :users)))

;;# TEST CASES
;;# --------------------------------------------------------------------------

(defflow can-create-user-when-admin
  {:init init}
  [admin (create-admin {:password "password"})]
  (log-in (:users/email admin) "password")

  [user (create-user)]

  (match? user (get-user (:users/id user))))

(defflow can-update-user-when-admin
  {:init init}
  [admin (create-admin {:password "password"})]
  (log-in (:users/email admin) "password")
  [user (create-user)]

  [updated-user (update-user {:user-id (:users/id user)
                              :data {:first-name "Updated"}})]

  (match? updated-user (get-user (:users/id user))))
