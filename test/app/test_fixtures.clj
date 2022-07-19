(ns app.test-fixtures
  (:require
   [app.test-core :refer [get-system]]
   [next.jdbc :as next-jdbc]))

(defn clean-db
  "Fixture for truncating PG database between tests"
  [test]
  (-> (get-system)
      (get-in [:xiana/postgresql :datasource])
      (next-jdbc/execute! ["DO $$ BEGIN
                         EXECUTE 'TRUNCATE TABLE '
                         || (SELECT string_agg(table_name::text, ',')
                             FROM information_schema.tables
                             WHERE table_schema = 'public'
                             AND table_type = 'BASE TABLE'
                             AND table_name != 'migrations'
                             AND table_name != 'seeds')
                         || ' CASCADE';
                       END; $$;"]))
  (test))
