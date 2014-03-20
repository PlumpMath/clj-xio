(ns com.lemonodor.xio-benchmarks
  (:require [clojure.java.io :as io]
            [clojure.test :refer :all]
            [criterium.core :as criterium]
            [com.lemonodor.xio :as xio]
            [me.raynes.fs :as fs]))

(def arrow-utf-8-bytes (map byte [-30 -122 -110]))
(def arrows "→→→→→→→→→→")
(def arrows-utf-8-bytes (.getBytes arrows))


;; (deftest ^:benchmark timing-tests
;;   (doseq [n (sort [(* 1024 1024 10) (* 1024 1024 1) (* 1024 100) 1024])]
;;     (let [f (fs/temp-file "slurp.dat")
;;           content (apply str (repeat n \A))]
;;       (.deleteOnExit f)
;;       (println "Data size is" n "bytes/chars")
;;       (testing "core/spit timing"
;;         (println "*** core/spit" n)
;;         (criterium/bench
;;          (spit f content))
;;         (is (= (fs/size f) n)))
;;       (testing "xio/spit timing"
;;         (println "*** xio/spit" n)
;;         (criterium/bench
;;          (xio/spit f content))
;;         (is (= (fs/size f) n)))
;;       (testing "xio/binary-spit [byte array] timing"
;;         (println "*** xio/binary-spit [byte array]" n)
;;         (let [bin-content (.getBytes content)]
;;           (criterium/bench
;;            (xio/binary-spit f bin-content))
;;           (is (= (fs/size f) n))))
;;       (testing "xio/binary-spit [vector] timing"
;;         (println "*** xio/binary-spit [vector]" n)
;;         (let [bin-content (vec (.getBytes content))]
;;           (criterium/bench
;;            (xio/binary-spit f bin-content))
;;           (is (= (fs/size f) n))))
;;       (testing "xio/binary-spit [byte array] with callback timing"
;;         (println "*** xio/binary-spit [byte array] :callback" n)
;;         (let [bin-content (.getBytes content)]
;;           (criterium/bench
;;            (xio/binary-spit f bin-content :callback (fn [_ __])))
;;           (is (= (fs/size f) n))))
;;       (testing "core/slurp timing"
;;         (println "*** core/slurp" n)
;;         (criterium/bench
;;          (slurp f))
;;         (is (= (count (slurp f)) n)))
;;       (testing "xio/slurp timing"
;;         (println "*** xio/slurp" n)
;;         (criterium/bench
;;          (xio/slurp f))
;;         (is (= (count (xio/slurp f)) n)))
;;       (testing "xio/binary-slurp timing"
;;         (println "*** xio/binary-slurp" n)
;;         (criterium/bench
;;          (xio/binary-slurp f))
;;         (is (= (count (xio/binary-slurp f)) n))))))
