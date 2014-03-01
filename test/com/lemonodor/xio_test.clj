(ns com.lemonodor.xio-test
  (:require [clojure.java.io :as io]
            [clojure.test :refer :all]
            [com.lemonodor.xio :as xio]
            [me.raynes.fs :as fs]))

(def arrow-utf-8-bytes (map byte [-30 -122 -110]))
(def arrows "→→→→→→→→→→")
(def arrows-utf-8-bytes (.getBytes arrows))


(deftest slurp-tests
  (testing "slurp from a file"
    (let [s (xio/slurp (io/file (io/resource "arrows.txt")))]
      (is (= (count s) 10))
      (is (= s arrows))))
  (testing "slurp from a URL"
    (let [s (xio/slurp (io/resource "arrows.txt"))]
      (is (= (count s) 10))
      (is (= s arrows))))
  (testing "slurp from an input stream"
    (let [s (xio/slurp (io/input-stream (io/resource "arrows.txt")))]
      (is (= (count s) 10))
      (is (= s arrows))))
  (testing "slurp from a reader"
    (let [s (xio/slurp (io/reader (io/resource "arrows.txt")))]
      (is (= (count s) 10))
      (is (= s arrows))))
  (testing "slurp with a buffer-size"
    (let [s (xio/slurp
             (io/resource "arrows.txt")
             :buffer-size 1)]
      (is (= (count s) 10))
      (is (= s arrows))))
  (testing "slurp with encoding"
    (let [s (xio/slurp
             (io/input-stream (io/resource "arrows.txt"))
             :encoding "US-ASCII")]
      (is (= (count s) 30))
      (is (= s (String. arrows-utf-8-bytes "US-ASCII")))))
  (testing "slurp with encoding and callback"
    (let [cb-args (atom [])
          s (xio/slurp
             (io/input-stream (io/resource "arrows.txt"))
             :encoding "US-ASCII"
             :callback (fn [& args]
                         (swap! cb-args conj args)))]
      (is (= (count s) 30))
      (is (= s (String. arrows-utf-8-bytes "US-ASCII")))
      (is (= @cb-args ['(30 30)]))))
  (testing "slurp with buffer-size and callback"
    (let [cb-args (atom [])
          s (xio/slurp
             (io/input-stream (io/resource "arrows.txt"))
             :buffer-size 4
             :callback (fn [& args]
                         (swap! cb-args conj args)))]
      (is (= (count s) 10))
      (is (= s arrows))
      (is (= @cb-args ['(4 4) '(4 8) '(2 10)])))))


(deftest binary-slurp-tests
  (testing "binary-slurp from a file"
    (let [bytes (xio/binary-slurp (io/file (io/resource "arrows.txt")))]
      (is (= (Class/forName "[B")
             (.getClass bytes)))
      (is (= (seq bytes) (seq arrows-utf-8-bytes)))))
  (testing "binary-slurp from a URL"
    (let [bytes (xio/binary-slurp (io/resource "arrows.txt"))]
      (is (= (Class/forName "[B")
             (.getClass bytes)))
      (is (= (seq bytes) (seq arrows-utf-8-bytes)))))
  (testing "binary-slurp from an input stream"
    (let [bytes (xio/binary-slurp (io/input-stream (io/resource "arrows.txt")))]
      (is (= (Class/forName "[B")
             (.getClass bytes)))
      (is (= (seq bytes) (seq arrows-utf-8-bytes)))))
  (testing "binary-slurp with a buffer-size and callback"
    (let [cb-args (atom [])
          bytes (xio/binary-slurp
                 (io/resource "arrows.txt")
                 :buffer-size 5
                 :callback (fn [& args] (swap! cb-args conj args)))]
      (is (= (Class/forName "[B")
             (.getClass bytes)))
      (is (= (seq bytes) (seq arrows-utf-8-bytes)))
      (is (= @cb-args ['(5 5) '(5 10) '(5 15) '(5 20) '(5 25) '(5 30)])))))


(deftest spit-tests
  (testing "spit to a file"
    (let [f (fs/temp-file "spit.out")]
      (.deleteOnExit f)
      (xio/spit f arrows)
      (is (= (slurp f) arrows))))
  (testing "spit to a writer"
    (let [f (fs/temp-file "spit.out")]
      (.deleteOnExit f)
      (with-open [w (io/writer f)]
        (xio/spit w arrows))
      (is (= (slurp f) arrows))))
  (testing "spit to an output stream"
    (let [f (fs/temp-file "spit.out")]
      (.deleteOnExit f)
      (with-open [os (io/output-stream f)]
        (xio/spit os arrows))
      (is (= (slurp f) arrows))))
  (testing "spit with an encoding"
    (let [f (fs/temp-file "spit.out")]
      (.deleteOnExit f)
      (xio/spit f (str "hello " arrows) :encoding "US-ASCII")
      (is (= (slurp f) "hello ??????????"))))
  (testing "spit with a buffer-size and callback"
    (let [f (fs/temp-file "spit.out")
          cb-args (atom [])]
      (.deleteOnExit f)
      (xio/spit
       f
       (str "hello " arrows)
       :buffer-size 5
       :callback (fn [& args] (swap! cb-args conj args)))
      (is (= (slurp f) (str "hello " arrows)))
      (is (= @cb-args ['(5 5) '(5 10) '(5 15) '(1 16)])))))


(deftest binary-spit-tests
  (testing "binary-spit to a file"
    (let [f (fs/temp-file "spit.out")]
      (.deleteOnExit f)
      (xio/binary-spit f arrows-utf-8-bytes)
      (is (= (slurp f) arrows))))
  (testing "binary-spit to an output stream"
    (let [f (fs/temp-file "spit.out")]
      (.deleteOnExit f)
      (with-open [os (io/output-stream f)]
        (xio/binary-spit os arrows-utf-8-bytes))
      (is (= (slurp f) arrows))))
  (testing "binary-spit with a vector"
    (let [f (fs/temp-file "spit.out")]
      (.deleteOnExit f)
      (with-open [os (io/output-stream f)]
        (xio/binary-spit os (vec arrows-utf-8-bytes)))
      (is (= (slurp f) arrows))))
  (testing "binary-spit with a buffer-size and callback"
    (let [f (fs/temp-file "spit.out")
          cb-args (atom [])]
      (.deleteOnExit f)
      (xio/binary-spit
       f
       arrows-utf-8-bytes
       :buffer-size 16
       :callback (fn [& args] (swap! cb-args conj args)))
      (is (= (slurp f) arrows))
      (is (= @cb-args ['(16 16) '(14 30)])))))


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
      (is (= (fs/size f) n))
      (let [[time _] (time-fn #(xio/spit f content))]
        (println "xio/spit took" time "s"))
      (is (= (fs/size f) n))
      (let [[time _] (time-fn #(xio/spit f content :callback (fn [_ __])))]
        (println "xio/spit with callback took" time "s"))
      (is (= (fs/size f) n))
      (let [bin-content (.getBytes content)
            [time _] (time-fn #(xio/binary-spit f bin-content))]
      (is (= (fs/size f) n))
        (println "xio/binary-spit [byte array] took" time "s"))
      (let [bin-content (vec (.getBytes content))
            [time _] (time-fn #(xio/binary-spit f bin-content))]
        (println "xio/binary-spit [vector] took" time "s"))
      (is (= (fs/size f) n))
      (let [bin-content (.getBytes content)
            [time _] (time-fn #(xio/binary-spit f bin-content
                                                :callback (fn [_ __])))]
        (println "xio/binary-spit [byte array] with callback took" time "s"))
      (is (= (fs/size f) n))
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
