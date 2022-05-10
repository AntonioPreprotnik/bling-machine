(ns app.boundary.penkala-helpers)

(defn cast-as [value as]
  [:cast value as])

(defn cast-attr-as [data attr as]
  (if (contains? data attr)
    (update data attr cast-as as)
    data))

