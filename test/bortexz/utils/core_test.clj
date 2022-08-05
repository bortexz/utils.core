(ns bortexz.utils.core-test
  (:require [clojure.test :refer [testing deftest is]]
            [bortexz.utils.core :as uc]))

(deftest chain-fx-test
  (testing "chain-fx works"
    (let [at (atom 0)]
      (is (= 1 (uc/chain-fx! at (fn [prev] (inc prev))))))))

(deftest chain-fx-vals-test
  (testing "chain-fx-vals! works"
    (let [at (atom (delay 0))]
      (is (= [0 1] (uc/chain-fx-vals! at (fn [prev] (inc prev))))))))
