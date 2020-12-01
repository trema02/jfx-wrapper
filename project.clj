(defproject de.trema02/jfx-wrapper "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "MIT-License"
            :url "https://opensource.org/licenses/mit-license.php" ; this is going to break
            :distribution :repo}
  :aot :all
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [proto-repl "0.3.1"]]
  :repl-options {:init-ns de.trema02.jfx-wrapper.core})
