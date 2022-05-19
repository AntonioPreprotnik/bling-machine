(ns backend.app.penkala
  (:require
   [app.domain.user :as user]
   [app.penkala :refer [with-transaction]]
   [backend.app.web.api.helpers :refer [command!]]
   [clojure.test :refer [use-fixtures]]
   [medley.core :as m]
   [state-flow.api :as flow :refer [flow]]
   [state-flow.assertions.matcher-combinators :refer [match?]]
   [state-flow.cljtest :refer [defflow]]
   [test-core :refer [get-system]]
   [test-fixtures :refer [clean-db]]))

(use-fixtures :each clean-db)

;; Suppresses clj-kondo unresolved symbol
(declare user-created user-created-modified rollback-transaction-check successful-transaction-check)

(def user-data
  {:email      "atd@vbt.com"
   :first-name "Frka1"
   :last-name  "Trle1"
   :zip        "10000"})

(def user-data-with-ns
  (update-keys user-data #(keyword "users" (name %))))

(defn init []
  (let [system (get-system)]
    {:system system}))

(defn create-user-twice []
  (flow "Create twice"
    (flow/swap-state
     (fn [{:keys [_ _] {penkala :penkala} :system :as state}]
       (let [data user-data
             uuid (m/random-uuid)
             user-data (assoc data :id uuid)]
         (try
           (with-transaction [p penkala]
             (user/insert p user-data)
             (user/insert p user-data))
           (catch Exception e (str "caught exception: " (.getMessage e))))
         state)))))

(defn insert-and-modify-user []
  (flow "Insert and modify"
    (flow/swap-state
     (fn [{:keys [_ _] {penkala :penkala} :system :as state}]
       (let [data user-data
             uuid (m/random-uuid)
             user-data (assoc data :id uuid)]
         (try
           (with-transaction [p penkala]
             (user/insert p user-data)
             (user/update-by-id! p (assoc user-data :first-name "Frka2") uuid))
           (catch Exception e (str "caught exception: " (.getMessage e))))
         state)))))

(defn get-user []
  (flow "Get all"
    (flow/swap-state
     (fn [{:keys [_ _] {funicular :app/funicular} :system :as state}]
       (let [all-users (command! funicular :api.user/get-all {})]
         (assoc state :first-user (first all-users)))))
    (flow/get-state :first-user)))

(defflow rollback-transaction-check
  {:init init}
  [_ (create-user-twice)]
  [user-created (get-user)]
  (match? {}
          (select-keys user-created [:users/email :users/first-name :users/last-name :users/zip])))

(defflow successful-transaction-check
  {:init init}
  [_ (insert-and-modify-user)]
  [user-created-modified (get-user)]
  (match? (assoc user-data-with-ns :users/first-name "Frka2")
          (select-keys user-created-modified [:users/email :users/first-name :users/last-name :users/zip])))

