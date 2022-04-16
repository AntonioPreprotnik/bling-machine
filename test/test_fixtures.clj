(ns test-fixtures
  (:require [next.jdbc :as next-jdbc]
            [test-core :refer [get-system]]))

(defn clean-db
  "Fixture for truncating PG database between tests"
  [test]
  (test)
  (-> (get-system)
      (get-in [:xiana/postgresql :datasource])
      (njdbc/execute! ["DO $$ BEGIN
                         EXECUTE 'TRUNCATE TABLE '
                         || (SELECT string_agg(table_name::text, ',')
                             FROM information_schema.tables
                             WHERE table_schema = 'public'
                             AND table_type = 'BASE TABLE'
                             AND table_name != 'migrations'
                             AND table_name != 'seeds')
                         || ' CASCADE';
                       END; $$;"])))
