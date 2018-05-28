(ns xl.db.redis-test
  (:require [clojure.test :refer :all]))

(deftest test-save
  (testing "save a row"
    (is (= 1 0))))