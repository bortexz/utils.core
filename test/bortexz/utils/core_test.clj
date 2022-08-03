(ns bortexz.utils.core-test
  (:require [clojure.test :refer [testing deftest is]]
            [bortexz.utils.core :as uc]))

(deftest chain-fx-test
  (testing "chain-fx works"
    (let [at (atom 0)]
      (is (= 1 (uc/chain-fx! at (fn [prev] (inc prev))))))))
