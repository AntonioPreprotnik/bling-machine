(ns shared.schema.common-test
  (:require
   [clojure.test :refer [deftest is]]
   [malli.util :as mu]
   [schema.common :as sut]))

(deftest remove-keys-namespaces-test
  (let [schema   [:map {:registry {::mid :int}}
                  ::mid
                  [:aa/id :string]]
        expected [:map {:registry {::mid :int}}
                  [:mid ::mid]
                  [:id :string]]]
    (is (mu/equals expected (sut/remove-keys-namespaces schema)))))
