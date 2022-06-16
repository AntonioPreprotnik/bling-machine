(ns app.backend.penkala
  (:require
   [com.verybigthings.funicular.anomalies :as anom]
   [com.verybigthings.penkala.env :refer [with-db]]
   [com.verybigthings.penkala.next-jdbc
    :as
    penkala-next-jdbc
    :refer
    [get-env]]
   [com.verybigthings.pgerrors.core :as pgerrors]
   [next.jdbc :as next-jdbc]
   [next.jdbc.result-set :as rs])
  (:import
   org.postgresql.util.PSQLException))

(defrecord Boundary [env])

(defn assoc-transaction [boundary t]
  (update boundary :env with-db t))

(defn get-db [boundary]
  (-> boundary :env force :com.verybigthings.penkala.env/db))

(defmacro with-transaction [[sym penkala opts] & body]
  `(next-jdbc/with-transaction [internal-sym# (get-db ~penkala) ~opts]
     (let [~sym (assoc-transaction ~penkala internal-sym#)]
       ~@body)))

(defn humanize [error-formatters error-data data]
  (let [constraint (:postgresql/constraint error-data)
        formatter (get error-formatters constraint)]
    (cond
      (fn? formatter) (formatter error-data data)
      formatter formatter
      :else (:postgresql.error/message error-data))))

(defn wrap-exception-handler [afn]
  (fn [& args]
    (try
      (apply afn args)
      (catch PSQLException e
        (let [[{error-formatters ::error-formatters} & _] args
              error-data    (pgerrors/extract-data e)
              humanized-message (humanize error-formatters error-data {})
              anomaly (anom/incorrect humanized-message error-data)]
          (throw (anom/->ex-info anomaly e)))))))

(def insert! (wrap-exception-handler penkala-next-jdbc/insert!))
(def update! (wrap-exception-handler penkala-next-jdbc/update!))
(def delete! (wrap-exception-handler penkala-next-jdbc/delete!))
(def select! (wrap-exception-handler penkala-next-jdbc/select!))
(def select-one! (wrap-exception-handler penkala-next-jdbc/select-one!))

(defn read-as-instant!
  "After calling this function, `next.jdbc.result-set/ReadableColumn`
  will be extended to `java.sql.Timestamp` so that any
  timestamp columns will automatically be read as `java.time.Instant`."
  []
  (extend-protocol rs/ReadableColumn
    java.sql.Timestamp
    (read-column-by-label [^java.sql.Timestamp v _]     (.toInstant v))
    (read-column-by-index [^java.sql.Timestamp v _2 _3] (.toInstant v))))

(defn init [{:keys [db] :as config}]
  (let [ds (get-in db [:datasource])
        env (assoc (get-env ds) ::error-formatters {})]
    (read-as-instant!)
    (assoc config :penkala (->Boundary env))))

