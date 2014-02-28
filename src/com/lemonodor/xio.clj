(ns com.lemonodor.xio
  (:require [clojure.java.io :as io]))


(defn binary-slurp
  "Opens a reader on f and reads all its contents, returning a string.
  See clojure.java.io/reader for a complete list of supported arguments."
  ([f & opts]
     (let [output (java.io.ByteArrayOutputStream.)
           buffer (make-array Byte/TYPE (get opts :buffer-size 16384))]
       (with-open [^java.io.InputStream input (apply io/input-stream f opts)]
         (loop []
           (let [size (.read input buffer)]
             (if (pos? size)
               (do
                 (.write output buffer 0 size)
                 (recur))
               (.toByteArray output))))))))
