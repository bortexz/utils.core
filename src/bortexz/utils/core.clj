(ns bortexz.utils.core
  (:require [better-cond.core :as bc]))

(defmacro while-let
  "Like clojure.core/while but accepts a vector of bindings as first expr. Only when all bindings
   are logical true the body executes and recurs back into the while.
   
   Supports multiple bindings and intermediary unconditional :let"
  [bindings & body]
  `(loop []
     (bc/when-let ~bindings
       ~@body
       (recur))))

(defmacro while-some
  "Like clojure.core/while but accepts a vector of bindings as first expr. Only when all bindings
   are non-nil the body executes and recurs back into the while.

   Supports multiple bindings and intermediary unconditional :let

   E.g consume a core.async/chan
   ```
   (while-some [v (<! ch)] ...)
   ```
   "
  [bindings & body]
  `(loop []
     (bc/when-some ~bindings
       ~@body
       (recur))))

(comment
  (macroexpand-1 '(while-let [v (<! ch)] v))
  (macroexpand-1 '(while-some [v (<! ch)] v)))

(defn chain-fx!
  "Given an atom `a` and a function `f` that accepts one argument, it will swap! a new `delay` onto `a` 
   that calls `f` with `(force current-atom-val)` as argument. Then, forces the new swapped-in delay.
   
   Useful to `chain` side-effects inside an atom."
  [a f]
  (force (swap! a (fn [fx] (delay (f (force fx)))))))

(defmacro assert-val
  "Assert and returns the value of `expr`"
  [expr msg]
  `(let [v# ~expr]
     (assert v# ~msg)
     v#))

(defn invert-comparator 
  "Inverts the given comparator `cmp`, returning a new comparator fn"
  [cmp] 
  (fn [a b] (cmp b a)))

(def ascending-comparator  
  "Ascending comparator fn. Equivalent to clojure.core/compare"
  compare)

(def descending-comparator
  "Descending comparator fn, inverts clojure.core/compare"
  (invert-comparator compare))
