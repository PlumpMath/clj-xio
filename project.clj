(defproject com.lemonodor/xio "0.2.3"
  :description "Extra IO functions."
  :url "http://github.com/clj-xio"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :deploy-repositories {"releases" :clojars}
  :dependencies [[org.clojure/clojure "1.5.1"]]
  :profiles {:dev {:dependencies [[criterium "0.4.3"]
                                  [me.raynes/fs "1.4.5"]]
                   :plugins [[lein-cloverage "1.0.2"]]
                   :test-selectors {:default (complement :benchmark)
                                    :benchmark :benchmark}
                   :jvm-opts ["-Xmx2g"]}})
