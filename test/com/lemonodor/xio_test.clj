(ns com.lemonodor.xio-test
  (:require [clojure.java.io :as io]
            [clojure.test :refer :all]
            [com.lemonodor.xio :as xio]))

(deftest a-test
  (testing "Reading from a file."
    (let [bytes (xio/binary-slurp (io/resource "test.bin"))]
      (is (= (Class/forName "[B")
             (.getClass bytes)))
      (is (= (seq bytes)
             (seq (byte-array (map byte [0 1 2 3 4 3 2 1 0 1 2 3 4])))))))
  (testing "Reading lots of data"
    (let [data (byte-array
                (map (comp byte #(- 127 (mod % 256))) (range 100000)))
          in (java.io.ByteArrayInputStream. data)
          bytes (xio/binary-slurp in)]
      (is (= (Class/forName "[B")
             (.getClass bytes)))
      (is (= (seq bytes)
             (seq data))))))
