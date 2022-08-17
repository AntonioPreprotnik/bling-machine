(ns app.shared.helpers)

(defn select-as-simple-keys [m ks]
  (persistent!
   (reduce
    (fn [acc k]
      (if (contains? m k)
        (assoc! acc (-> k name keyword) (get m k))
        acc))
    (transient {})
    ks)))
