(ns backend.app.web.api.handlers.user-test
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
(declare user-create user-created create-and-get new-user get-new-user transactions-check)

(defn init []
  (let [system (get-system)]
    {:system system}))

(defn create-twice []
  (flow "Create twice"
    (flow/swap-state
     (fn [{:keys [_ _] {penkala :penkala} :system :as state}]
       (let [data {:email      "atd@vbt.com"
                   :first-name "Frka1"
                   :last-name  "Trle1"
                   :zip        "10000"}
             uuid (m/random-uuid)
             user-data (assoc data :id uuid)]
         (try
           (with-transaction [p penkala]
             (user/insert p user-data)
             (user/insert p user-data))
           (catch Exception e (str "caught exception: " (.getMessage e))))
         (assoc state :new-twice new-user))))
    (flow/get-state :new-twice)))

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

(defflow transactions-check
  {:init init}
  [_ (create-twice)]
  [get-new-user (create-user)]
  (match? {:users/email      "atd@vbt.com"
           :users/first-name "Frka1"
           :users/last-name  "Trle1"
           :users/zip        "10000"}
          (select-keys get-new-user [:users/email :users/first-name :users/last-name :users/zip])))

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


