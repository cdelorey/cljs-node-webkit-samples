(defproject cljs-node-webkit-examples "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  
  :source-paths ["src/clj"]
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [domina "1.0.2-SNAPSHOT"]]
  
  ;; lein-cljsbuild plugin to build a CLJS project
  :plugins [[lein-cljsbuild "0.3.2"]]
  
  ;; cljsbuild options configuration
  :cljsbuild
  {:builds

   ;; hello-world build
   {:hello-world
    {:source-paths ["src/hello_world"]
    :compiler
    {:output-to "resources/public/hello_world/js/hello_world.js"
    :optimizations :whitespace
    :pretty-print true}}
    
    ;; menus build
    :menus
    {:source-paths ["src/menus"]
    :compiler
    {:output-to "resources/public/menus/menus.js"
    :optimizations :whitespace
    :pretty-print true}}
    
    ;; menus build
    :mini-code-edit
    {:source-paths ["src/mini_code_edit"]
    :compiler
    {:output-to "resources/public/mini_code_edit/editor.js"
    :externs ["resources/public/mini_code_edit/externs.js"]
    :optimizations :whitespace
    :pretty-print true}}
   
   ;; frameless-window build
    :frameless-window
    {:source-paths ["src/frameless_window"]
    :compiler
    {:output-to "resources/public/frameless_window/js/frameless_window.js"
    :optimizations :whitespace
    :pretty-print true}}}
   
   })