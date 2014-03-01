(ns com.lemonodor.xio-test
  (:require [clojure.java.io :as io]
            [clojure.test :refer :all]
            [com.lemonodor.xio :as xio]
            [me.raynes.fs :as fs]))

(deftest binary-slurp-tests
  (testing "Slurping from a file"
    (let [bytes (xio/binary-slurp (io/resource "test.bin"))]
      (is (= (Class/forName "[B")
             (.getClass bytes)))
      (is (= (seq bytes)
             (seq (byte-array (map byte [0 1 2 3 4 3 2 1 0 1 2 3 4])))))))
  (testing "Slurping lots of data from a ByteArrayInputStream"
    (let [data (byte-array
                (map (comp byte #(- 127 (mod % 256))) (range 100000)))
          in (java.io.ByteArrayInputStream. data)
          bytes (xio/binary-slurp in)]
      (is (= (Class/forName "[B")
             (.getClass bytes)))
      (is (= (seq bytes)
             (seq data))))))


(defn time-fn [fn]
  (let [start-time (System/nanoTime)
        result (fn)]
    (dotimes [_ 100] (fn))
    [(/ (- (System/nanoTime) start-time) 1000000000.0)
     result]))


(deftest timing-tests
  (testing "slurp timing"
    (let [f (fs/temp-file "slurp.dat")
          n (* 1024 1024 1)
          content (apply str (repeat n \A))]
      (.deleteOnExit f)
      (let [[time _] (time-fn #(spit f content))]
        (println "core/spit took" time "s"))
      (let [[time _] (time-fn #(xio/spit f content))]
        (println "xio/spit took" time "s"))
      (let [[time _] (time-fn #(xio/spit f content :callback (fn [_ __])))]
        (println "xio/spit with callback took" time "s"))
      (let [bin-content (.getBytes content)
            [time _] (time-fn #(xio/binary-spit f bin-content))]
        (println "xio/binary-spit [byte array] took" time "s"))
      (let [bin-content (.getBytes content)
            [time _] (time-fn #(xio/binary-spit f bin-content :callback (fn [_ __])))]
        (println "xio/binary-spit [byte array] with callback took" time "s"))
      (let [bin-content (vec (.getBytes content))
            [time _] (time-fn #(xio/binary-spit f bin-content :callback (fn [_ __])))]
        (println "xio/binary-spit [vector] with callback took" time "s"))
      (let [[time data] (time-fn #(slurp f))]
        (is (= (count data) n))
        (println "core/slurp took" time "s"))
      (let [[time data] (time-fn #(xio/slurp f))]
        (is (= (count data) n))
        (println "xio/slurp took" time "s"))
      (let [[time data] (time-fn #(xio/binary-slurp f))]
        (is (= (count data) n))
        (println "xio/binary-slurp took" time "s"))
      )))
