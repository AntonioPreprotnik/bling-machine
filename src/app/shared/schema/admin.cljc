(ns shared.schema.admin)

(def registry
  {:app.input.login
   [:map
    [:email :app/email]
    [:password :password.rules/length]]
   :password.rules/length
   [:string {:min 8
             :error/message "Must be 8 characters"}]})
