(ns schema.common
  (:require malli.util))

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

(def registry
  {:app/email Email
   :app/jwt :string
   :app/timestamp :any})
