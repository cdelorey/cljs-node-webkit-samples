(defproject cljs-node-webkit-examples "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  
  :source-paths ["src/clj"]
  :dependencies [[org.clojure/clojure "1.5.1"]]
  
  ;; lein-cljsbuild plugin to build a CLJS project
  :plugins [[lein-cljsbuild "0.3.2"]]
  
  ;; cljsbuild options configuration
  :cljsbuild
  {:builds

   ;; hello-world build
   {:hello-world
    {:source-paths ["src/cljs/hello_world"]
    :compiler
    {:output-to "resources/public/hello_world/js/hello_world.js"
    :optimizations :whitespace
    :pretty-print true}}}})
  
