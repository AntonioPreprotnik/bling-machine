(ns shared.schema-test
  (:require
   [clojure.test :refer [deftest are testing]]
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

(deftest phone-test
  (testing "valid phone numbers"
    (are [phone] (m/validate sut/Phone phone)
      "79150554950"
      "123456789012345"))
  (testing "invalid phone numbers"
    (are [phone] (not (m/validate sut/Phone phone))
      "+155533231234"
      "(555)-3323"
      "79150554950_"
      "1234567890123456")))


