# simple-spy

A simple spy library for Clojure.

I've been learning Clojure for some time, and I wanted to challenge myself to create a spy framework.

## Usage

Include in your project:

    [baritonehands/simple-spy "0.1.0"]

Require in your Clojure namespace:

```clojure
(ns com.example.your-application
  (:require [baritonehands.simple-spy :as spy :include-macros true])) ; Verify is a macro
```

Create a spy:

```clojure
(def my-fn (spy/create {:foo "bar"})) ; Constantly return a single value
(def my-fn (spy/create (fn [& more] ...)) ; Provide a custom implementation to be called
```


List the calls:

```clojure
(my-fn 1 2 3)
(my-fn)
(my-fn :a :b)
(my-fn 1 2 3)

(spy/calls my-fn)
=> [(1 2 3) nil (:a :b) (1 2 3)]
```


Verify your spec:

```clojure
(spy/verify my-fn 1 2 3) ; Macro that returns number of calls
=> 2

(spy/verify my-fn number? even? odd?) ; Also accepts predicates
=> 2

(spy/verify my-fn 3 string? (every-pred odd? keyword?)) ; Something definitely false throws
AssertionError 
No matching call found:
  Expected: (3 string? (every-pred odd? keyword?))
  Calls: (1 2 3), (), (:a :b), (1 2 3)
```


## License

Copyright © 2016 Brian Gregg

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
