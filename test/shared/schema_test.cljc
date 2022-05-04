(ns shared.schema-test
  (:require
   [clojure.test :refer [are deftest testing]]
   [malli.core :as m]
   [schema.common :as sut]))

(deftest email-test
  (testing "valid emails"
    (are [email] (m/validate sut/Email email)
      "j@ya.ru"
      "j.k@ya.ru"))
  (testing "invalid emails"
    (are [email] (not (m/validate sut/Email email))
      ""
      "j@ya3 12"
      "j/wtf@ya"
      "j.k")))


