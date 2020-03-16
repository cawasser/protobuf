(ns protobuf.examples.syntax-3-test
  (:require
    [clojure.test :refer :all]
    [protobuf.core :as protobuf])
  (:import
    (com.google.protobuf ByteString)
    (protobuf.examples.message3 Message3 Person3)))



(def person {:id   7
             :name "Test Person"})
(def content {:content "This is the test content"})



(deftest example-syntax-3-person-test
  (is (= person
        (into {} (protobuf/create Person3 person)))))

(deftest example-syntax-3-round-trip-test
  (let [p (protobuf/create Person3 person)
        b (protobuf/->bytes p)]
    (= p (protobuf/bytes-> p b))))



(deftest example-syntax-3-nested-message
  (let [p (protobuf/create Message3 {:sender  (protobuf/create Person3 person)
                                     :content (:content content)})]
    (is (= "Test Person" (get-in p [:sender :name])))))



(deftest example-syntax-3-nested-message-round-trip-test
  (let [p (protobuf/create Message3 {:sender  (protobuf/create Person3 person)
                                     :content (:content content)})
        b (protobuf/->bytes p)]
    (= p (protobuf/bytes-> p b))))



(comment
  ; trying stuff at the REPL

  (protobuf/create Message3 {:sender  (protobuf/create Person3 person)
                             :content (:content content)})

  ())
