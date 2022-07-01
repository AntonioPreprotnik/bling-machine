(ns app.backend.web.api.handlers.user-test
  (:require
   [app.backend.web.api.helpers :refer [command!]]
   [app.test-core :refer [get-system]]
   [app.test-fixtures :refer [clean-db]]
   [clojure.test :refer [use-fixtures]]
   [state-flow.api :as flow :refer [flow]]
   [state-flow.assertions.matcher-combinators :refer [match?]]
   [state-flow.cljtest :refer [defflow]]))

(use-fixtures :each clean-db)

;; Suppresses clj-kondo unresolved symbol
(declare user-create user-created create-and-get new-user get-new-user)

(def user-data
  {:email      "atd@vbt.com"
   :first-name "Frka1"
   :last-name  "Trle1"})

(def user-data-with-ns
  (update-keys user-data #(keyword "users" (name %))))

(defn init []
  (let [system (get-system)]
    {:system system}))

(defn create-user []
  (flow "Create user"
    (flow/swap-state
     (fn [{:keys [_ _] {funicular :app/funicular} :system :as state}]
       (let [new-user (command! funicular :api.user/create user-data)]
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
  (match? user-data-with-ns
          (select-keys  user-created [:users/email :users/first-name :users/last-name :users/zip])))

(defflow create-and-get
  {:init init}
  [_ (create-user)]
  [get-new-user (get-user)]
  (match? user-data-with-ns
          (select-keys get-new-user [:users/email :users/first-name :users/last-name :users/zip])))


