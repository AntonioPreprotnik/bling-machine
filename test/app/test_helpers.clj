(ns app.test-helpers
  (:require
   [app.test-core :refer [state*]]))

(defn get-unique-integer
  "Gets unique positive integer for the test system.
   On every new test suite run, unique integer state is reseted."
  []
  (reset! state* (update @state* :unique-integer inc))
  (:unique-integer @state*))
