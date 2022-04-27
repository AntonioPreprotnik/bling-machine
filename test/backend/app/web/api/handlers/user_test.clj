(ns backend.app.web.api.handlers.user-test
  (:require [backend.app.web.api.helpers :refer [command!]]
            [clojure.test :refer [use-fixtures]]
            [next.jdbc :as next-jdbc]
            [state-flow.api :as flow :refer [flow]]
            [state-flow.assertions.matcher-combinators :refer [match?]]
            [state-flow.cljtest :refer [defflow]]
            [test-core :refer [get-system]]))

(defn clean-db
  "Fixture for truncating PG database between tests"
  [test]
  (test)
  (let [ds (-> (get-system) :xiana/postgresql :datasource)]
    (next-jdbc/execute! ds ["DELETE FROM users;"])))

(use-fixtures :each clean-db)

;; Suppresses clj-kondo unresolved symbol
(declare user-create user-created create-and-get new-user get-new-user)

(defn init []
  (let [system (get-system)]
    {:system system}))

(defn create-user []
  (flow "Create user"
    (flow/swap-state
     (fn [{:keys [_ _] {funicular :app/funicular} :system :as state}]
       (let [new-user (command! funicular :api.user/create {:email      "atd@vbt.com"
                                                            :first-name "Frka1"
                                                            :last-name  "Trle1"
                                                            :zip        "10000"})]
         (assoc state :new-user new-user))))
    (flow/get-state :new-user)))

(defn get-user []
  (flow "Get all"
    (flow/swap-state
     (fn [{:keys [_ _] {funicular :app/funicular} :system :as state}]
       (let [all-users (command! funicular :api.user/get-all {})]
         (assoc state :first-user (first all-users)))))
    (flow/get-state :first-user)))

(defflow user-create
  {:init init}
  [user-created (create-user)]
  (match? {:users/email      "atd@vbt.com"
           :users/first-name "Frka1"
           :users/last-name  "Trle1"
           :users/zip        "10000"}
          (select-keys  user-created [:users/email :users/first-name :users/last-name :users/zip])))

(defflow create-and-get
  {:init init}
  [_ (create-user)]
  [get-new-user (get-user)]
  (match? {:users/email      "atd@vbt.com"
           :users/first-name "Frka1"
           :users/last-name  "Trle1"
           :users/zip        "10000"}
           ;:user/id          uuid?}
          (select-keys get-new-user [:users/email :users/first-name :users/last-name :users/zip])))
                                ;:user/id])))


