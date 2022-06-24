(ns app.shared.schema.common-test
  (:require
   [app.shared.schema.common :as sut]
   [clojure.test :refer [deftest is]]
   [malli.util :as mu]))

(deftest remove-keys-namespaces-test
  (let [schema   [:map {:registry {::mid :int}}
                  ::mid
                  [:aa/id :string]]
        expected [:map {:registry {::mid :int}}
                  [:mid ::mid]
                  [:id :string]]]
    (is (mu/equals expected (sut/remove-keys-namespaces schema)))))
