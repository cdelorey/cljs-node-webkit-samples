(ns cljs-node-webkt-examples.testing)

(defn this-function []
  (this-as this
           (+ 4 5)))