(ns com.lemonodor.xio
  (:require [clojure.java.io :as io])
  (:import (java.io ByteArrayInputStream CharArrayReader File
                    FileInputStream FileOutputStream InputStream
                    InputStreamReader OutputStream OutputStreamWriter
                    Reader StringReader Writer)))


(set! *warn-on-reflection* true)


(def
    ^{:doc "Type object for a Java primitive byte array."
      :private true
      }
 byte-array-type (class (make-array Byte/TYPE 0)))

(def
    ^{:doc "Type object for a Java primitive char array."
      :private true}
 char-array-type (class (make-array Character/TYPE 0)))


(defn- ^String encoding [opts]
  (or (:encoding opts) "UTF-8"))

(defn- buffer-size [opts]
  (or (:buffer-size opts) 1024))


(defmulti
  ^{:doc "Internal helper for copy"
    :private true
    :arglists '([input output opts])}
  do-copy
  (fn [input output opts] [(type input) (type output)]))

(defmethod do-copy [InputStream OutputStream]
  [^InputStream input ^OutputStream output opts]
  (let [buffer (make-array Byte/TYPE (buffer-size opts))]
    (loop [total 0]
      (let [size (.read input buffer)]
        (when (pos? size)
          (let [total (+ total size)]
            (.write output buffer 0 size)
            (when-let [callback (:callback opts)]
              (callback size total))
              (recur total)))))))

(defmethod do-copy [InputStream Writer] [^InputStream input ^Writer output opts]
  (let [^"[C" buffer (make-array Character/TYPE (buffer-size opts))
        in (InputStreamReader. input (encoding opts))]
    (loop [total 0]
      (let [size (.read in buffer 0 (alength buffer))]
        (if (pos? size)
          (let [total (+ total size)]
            (.write output buffer 0 size)
            (when-let [callback (:callback opts)]
              (callback size total))
            (recur total)))))))

(defmethod do-copy [InputStream File] [^InputStream input ^File output opts]
  (with-open [out (FileOutputStream. output)]
    (do-copy input out opts)))

(defmethod do-copy [Reader OutputStream]
  [^Reader input ^OutputStream output opts]
  (let [^"[C" buffer (make-array Character/TYPE (buffer-size opts))
        out (OutputStreamWriter. output (encoding opts))]
    (loop [total 0]
      (let [size (.read input buffer)]
        (if (pos? size)
          (let [total (+ total size)]
            (.write out buffer 0 size)
            (when-let [callback (:callback opts)]
              (callback size total))
            (recur total))
          (.flush out))))))

(defmethod do-copy [Reader Writer] [^Reader input ^Writer output opts]
  (let [^"[C" buffer (make-array Character/TYPE (buffer-size opts))]
    (loop [total 0]
      (let [size (.read input buffer)]
        (when (pos? size)
          (let [total (+ total size)]
            (.write output buffer 0 size)
            (when-let [callback (:callback opts)]
              (callback size total))
            (recur total)))))))

(defmethod do-copy [Reader File] [^Reader input ^File output opts]
  (with-open [out (FileOutputStream. output)]
    (do-copy input out opts)))

(defmethod do-copy [File OutputStream] [^File input ^OutputStream output opts]
  (with-open [in (FileInputStream. input)]
    (do-copy in output opts)))

(defmethod do-copy [File Writer] [^File input ^Writer output opts]
  (with-open [in (FileInputStream. input)]
    (do-copy in output opts)))

(defmethod do-copy [File File] [^File input ^File output opts]
  (with-open [in (-> input FileInputStream. .getChannel)
              out (-> output FileOutputStream. .getChannel)]
    (let [sz (.size in)]
      (loop [pos 0]
        (let [bytes-xferred (.transferTo in pos (- sz pos) out)
              pos (+ pos bytes-xferred)]
          (when-let [callback (:callback opts)]
            (callback bytes-xferred pos))
          (when (< pos sz)
            (recur pos)))))))

(defmethod do-copy [String OutputStream]
  [^String input ^OutputStream output opts]
  (do-copy (StringReader. input) output opts))

(defmethod do-copy [String Writer] [^String input ^Writer output opts]
  (do-copy (StringReader. input) output opts))

(defmethod do-copy [String File] [^String input ^File output opts]
  (do-copy (StringReader. input) output opts))

(defmethod do-copy [char-array-type OutputStream]
  [input ^OutputStream output opts]
  (do-copy (CharArrayReader. input) output opts))

(defmethod do-copy [char-array-type Writer] [input ^Writer output opts]
  (do-copy (CharArrayReader. input) output opts))

(defmethod do-copy [char-array-type File] [input ^File output opts]
  (do-copy (CharArrayReader. input) output opts))

(defmethod do-copy [byte-array-type OutputStream]
  [^"[B" input ^OutputStream output opts]
  (do-copy (ByteArrayInputStream. input) output opts))

(defmethod do-copy [byte-array-type Writer] [^"[B" input ^Writer output opts]
  (do-copy (ByteArrayInputStream. input) output opts))

(defmethod do-copy [byte-array-type File] [^"[B" input ^Writer output opts]
  (do-copy (ByteArrayInputStream. input) output opts))


(defn copy
  "Copies input to output.  Returns nil or throws IOException.
  Input may be an InputStream, Reader, File, byte[], or String.
  Output may be an OutputStream, Writer, or File.

  Options are key/value pairs and may be one of

    :buffer-size  buffer size to use, default is 1024.
    :encoding     encoding to use if converting between
                  byte and char streams.
    :callback     a function of 2 arguments: the number of bytes/characters
                  copied since the last call, and the total number of bytes/
                  characters.

  Does not close any streams except those it opens itself
  (on a File)."
  [input output & opts]
  (do-copy input output (when opts (apply hash-map opts))))


(defn binary-slurp
  "Opens an input stream on f and reads all its contents, returning a
  string.  See clojure.java.io/input-stream for a complete list of
  supported arguments."
  ([f & opts]
     (let [output (java.io.ByteArrayOutputStream.)]
       (apply copy (apply io/input-stream f opts) output opts)
       (.toByteArray output))))
