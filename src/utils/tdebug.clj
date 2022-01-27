(ns ^:no-doc tdebug)


(defn trace>>
  "Wrapper for `tap>` that can be used in the middle of the `->>` threading macro like this:
  `(->> data
       (fn1 )
       (trace>> :test)
       (fn2))`
   With three arguments the third argument provides the function that is applied on the `value` before it is 'tapped'.
   Can be used outside of the thread macro like this: `(trace>> :comment value)`"
 ([key value] (tap> {key value}) value)
 ([key value fn] (tap> {key (fn value)}) value))

(defn trace>
  "Wrapper for `tap>` that can be used in the middle of the `->` threading macro like this:
    `(-> data
       (fn1 )
       (trace> :test)
       (fn2))`
   With three arguments the third argument provides the function that is applied on the `value` before it is 'tapped'.
   Can be used outside of the thread macro like this: `(trace> value :comment )`"
  ([value key] (tap> {key value}) value)
  ([value key fn] (tap> {key (fn value)}) value))


