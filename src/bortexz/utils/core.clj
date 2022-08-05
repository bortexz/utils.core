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

(defn chain-fx!
  "Given an atom `a` and a function `f` that accepts one argument, it will swap! a new `delay` onto `a` 
   that calls `f` with `(force current-atom-val)` as argument. Then, forces the new swapped-in delay.
   
   Useful to `chain` side-effects inside an atom."
  [a f]
  (force
   (swap! a (fn [fx]
              (delay
               (f (force fx)))))))

(defn chain-fx-vals!
  "Like [[chain-fx!]] but returns [(force <old val>) (force <new val>)]"
  [a f]
  (let [[o n] (swap-vals! a (fn [fx]
                              (delay
                               (f (force fx)))))]
    [(force o) (force n)]))

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

(defn uncaught-exception
  "Calls current thread's uncaught exception handler with exception `ex`. Returns nil."
  [ex]
  (-> (Thread/currentThread)
      .getUncaughtExceptionHandler
      (.uncaughtException (Thread/currentThread) ex))
  nil)

(defn set-uncaught-exception-handler!
  "Configures uncaught exception handler to `ex-handler`, 2-arity fn that will be called with the thread
   and the exception thrown when an uncaught exception happens.
   See: https://stuartsierra.com/2015/05/27/clojure-uncaught-exceptions"
  [ex-handler]
  (Thread/setDefaultUncaughtExceptionHandler
   (reify Thread$UncaughtExceptionHandler
     (uncaughtException [_ thread ex]
       (ex-handler thread ex)))))

(comment
  (set-uncaught-exception-handler! (fn [t ex] (println (.getName t) ex)))
  (uncaught-exception (ex-info "Uncaught Exception" {}))
  )
