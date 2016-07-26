;   Copyright (c) Brian Gregg. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file LICENSE at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns baritonehands.simple-spy-test
  (:require [clojure.test :refer :all]
            [baritonehands.simple-spy :refer :all :include-macros true]))

(deftest create-test
  (testing "should return fn with meta"
    (let [f (create nil)]
      (is (fn? f))
      (is @(:baritonehands.simple-spy/calls (meta f)) [])))
  (testing "should return value when called"
    (let [f (create 42)]
      (is (= (f 1 2 3) 42))
      (is (= (f :a) 42))
      (is (= (f) 42))))
  (testing "should call fn when called"
    (let [f (create #(identity %&))]
      (is (= (f 1 2 3) '(1 2 3)))
      (is (= (f :a) '(:a)))
      (is (= (f) nil)))))

(deftest calls-test
  (testing "should return calls key"
    (let [f (create nil)]
      (is (calls f) [])))
  (testing "should track calls"
    (let [f (create nil)]
      (f 1 2 3)
      (f :a :b :c)
      (f)
      (is (calls f) ['(1 2 3) '(:a :b :c) nil]))))

(deftest pred-or-eq?-test
  (testing "should handle value"
    (is (pred-or-eq? 1 1))
    (is (not (pred-or-eq? 1 2)))
    (is (pred-or-eq? [:foo "bar"] [:foo "bar"]))
    (is (pred-or-eq? {:foo "bar"} {:foo "bar"})))
  (testing "should handle predicate"
    (is (pred-or-eq? 1 number?))
    (is (pred-or-eq? 1 odd?))
    (is (not (pred-or-eq? 1 even?)))
    (is (pred-or-eq? [:foo "bar"] vector?))
    (is (pred-or-eq? [:foo "bar"] sequential?))
    (is (pred-or-eq? {:foo "bar"} #(contains? % :foo)))))

(deftest verify-test
  (testing "should handle single match"
    (let [f (create nil)]
      (f 1 2 3)
      (f :a :b :c)
      (f)
      (is (= (verify f 1 2 3) 1))
      (is (= (verify f :a :b :c) 1))
      (is (= (verify f) 1))))
  (testing "should handle multiple match"
    (let [f (create nil)]
      (f 1 2 3)
      (f 1 2 3)
      (is (= (verify f 1 2 3) 2))))
  (testing "should handle error"
    (let [f (create nil)]
      (f :a)
      (is (thrown? AssertionError (verify f 1 2 3)))
      (is (thrown? AssertionError (verify f string?))))))
