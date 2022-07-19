(ns app.shared.schema.user
  (:require
   [app.shared.schema.common :as sc]
   [malli.util :as mu]))

(def User
  [:map
   [:users/id :uuid]
   [:users/email sc/Email]
   [:users/first-name :string]
   [:users/last-name :string]])

(def InputCreate
  (-> User sc/remove-keys-namespaces (mu/dissoc :id)))

(def InputUpdateData
  (-> User
      sc/remove-keys-namespaces
      mu/optional-keys))

(def InputUpdate
  [:map
   [:user-id :uuid]
   [:data InputUpdateData]])

(def registry
  {:app/user User
   :app/users [:vector :app/user]
   :app.input.user/create InputCreate
   :app.input.user/update InputUpdate
   :app.input.user/one [:map [:user-id :uuid]]})
