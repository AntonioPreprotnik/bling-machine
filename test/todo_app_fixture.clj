(ns todo-app-fixture
  (:require
    [todo-app.core :refer [->system app-cfg]]))

(defn std-system-fixture
  [config f]
  (with-open [_ (->system (merge app-cfg config))]
    (f)))

