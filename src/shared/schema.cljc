(ns schema
  (:require
   [lambdaisland.regal :as regal]
   [malli.core :as m]
   [malli.util :as u]))

#?(:cljs
   (defn parse-price [val]
     (let [str-val (str val)
           numeric (js/parseFloat str-val)]
       (when-not (js/isNaN numeric)
         (-> numeric
             (* 100)
             (.toFixed 0)))))
   :clj
   (defn parse-price [val]
     (when (re-find #"^-?\d+\.?\d*$" val)
       (let [numeric (read-string val)]
         (-> numeric
             (* 100)
             int)))))

(def registry
  (merge
   (m/default-schemas)
   (u/schemas)
   {;user
    ::email
    [:and
     :string
     [:re {:error/message "Must be a valid email"}
      (regal/regex [:cat :start :non-whitespace [:+ :any] "@" [:+ :any] "." [:repeat :word 2 10] :end])]]

    :password.rules/length
    [:string {:min 8
              :error/message "Must be 8 characters"}]

    :app/jwt [:string]

    ::timestamp :any

    :users/id [:uuid]
    :users/first-name [:string]
    :users/last-name [:string]
    :users/zip [:string]

    :app/user
    [:map
     :users/id
     [:users/email ::email]
     :users/first-name
     :users/last-name
     :users/zip]

    :app/users
    [:vector
     :app/user]

    :app.input.user/create
    [:map
     [:email ::email]
     [:first-name :users/first-name]
     [:last-name :users/last-name]
     [:zip :users/zip]]

    :app.input.user/update
    [:map
     [:first-name :users/first-name]
     [:last-name :users/last-name]
     [:zip :users/zip]]

    :app.input.user/one
    [:map
     [:user-id :uuid]]

    :app.input.login
    [:map
     [:email ::email]
     [:password :password.rules/length]]}))
