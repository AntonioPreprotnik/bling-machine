(ns schema.common
  (:require
   [malli.util]
   [cljc.java-time.extn.predicates :refer [local-date?]]))

(defn remove-keys-namespaces
  "Turns `[:map [:a/x :int]]` into `[:map [:x :int]]`.
  Doesn't handle nested maps."
  ([schema] (remove-keys-namespaces schema nil))
  ([schema opts]
   (malli.util/transform-entries
    schema
    #(map
      (fn [[key opts val]]
        [(if (keyword? key) (-> key name keyword) key)
         opts
         val])
      %)
    opts)))

(def Email
  [:and {:error/message "Please enter a valid email"}
   :string [:re #"^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$"]])

(def Phone
  [:and :string [:re #"^\d{10,15}$"]])

(def Date
  [:fn {:error/message "Wrong date value."}
   local-date?])

(def registry
  {:app/email Email
   :app/phone Phone
   :app/date  Date

   :app/jwt :string

   :app/timestamp :any})
