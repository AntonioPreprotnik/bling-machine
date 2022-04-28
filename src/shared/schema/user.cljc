(ns schema.user
  (:require
   [malli.util :as mu]
   [schema.common :as sc]))

(def User
  [:map
   [:users/id :uuid]
   [:users/email sc/Email]
   [:users/first-name :string]
   [:users/last-name :string]])

(def InputCreate
  (-> User sc/remove-keys-namespaces (mu/dissoc :id)))

(def InputUpdate
  [:map
   [:data (-> InputCreate (mu/dissoc :email) mu/optional-keys)]
   [:user-id :uuid]])

(def registry
  {:app/user              User
   :app/users [:vector :app/user]
   :app.input.user/create InputCreate
   :app.input.user/update InputUpdate
   :app.input.user/one    [:map [:user-id :uuid]]})
