;   Copyright (c) Brian Gregg. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file LICENSE at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns ^{:doc "A simple spy library for Clojure."
      :author "Brian Gregg"}
  baritonehands.simple-spy)

(defn create
  "Creates a spy function which always returns fv or calls (apply fv args) if fv is a function."
  [fv]
  (let [calls (atom [])]
    (with-meta
      (fn [& more]
        ; Append a set of arguments
        (swap! calls conj more)
        (if (fn? fv)
          (apply fv more)
          fv))
      {::calls calls})))

(defn pred-or-eq? [v pred-eq]
  (if (fn? pred-eq)
    (pred-eq v)
    (= v pred-eq)))

(defn calls
  "Returns the vector of calls made to the spy f."
  [f] @(::calls (meta f)))

(defmacro verify
  "Macro which returns the count of calls to spy f that match spec exactly.
  Values must be equal or predicates must return true in spec."
  [f & spec]
  `(let [vs# (vector ~@spec)
         cnt# (count
                (filter (fn [call#]
                          (and (= (count call#) (count vs#))
                               (every? true? (map pred-or-eq? call# vs#))))
                        (calls ~f)))]
     (when-not (> cnt# 0)
       (throw (AssertionError.
                (str "\nNo matching call found:"
                     "\n  Expected: " '~spec
                     "\n  Calls: " (clojure.string/join ", " (map #(if (nil? %) "()" (str %)) (calls ~f))) "\n"))))

     cnt#))
