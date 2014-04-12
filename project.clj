(defproject com.lemonodor/xio "0.2.1"
  :description "Extra IO functions."
  :url "http://github.com/clj-xio"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]]
  :profiles {:dev {:dependencies [[me.raynes/fs "1.4.5"]]
                   :test-selectors {:default (complement :benchmark)
                                    :benchmark :benchmark}
                   :jvm-opts ["-Xmx2g"]}})
