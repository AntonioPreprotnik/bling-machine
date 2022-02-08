(ns resolve-util)

(defn- serialized-require
  "Like 'require', but serializes loading.
  Interim function preferred over 'require' for known asynchronous loads.
  Future changes may make these equivalent."
  {:added "1.10"}
  [& args]
  (locking clojure.lang.RT/REQUIRE_LOCK
    (apply require args)))

(defn req-resolve
  "Resolves namespace-qualified sym per 'resolve'. If initial resolve
fails, attempts to require sym's namespace and retries."
  {:added "1.10"}
  [sym]
  (if (qualified-symbol? sym)
    (or (resolve sym)
      (do (-> sym namespace symbol serialized-require)
          (resolve sym)))
    (throw (IllegalArgumentException. (str "Not a qualified symbol: " sym)))))
