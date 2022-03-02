CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
--;;
CREATE EXTENSION IF NOT EXISTS citext;
--;;
CREATE TABLE users (
  id uuid NOT NULL,
  first_name character varying(255) NOT NULL,
  last_name character varying(255) NOT NULL,
  email citext NOT NULL,
  inserted_at timestamp without time zone DEFAULT NOW(),
  updated_at timestamp without time zone DEFAULT NOW(),
  zip character varying(255) NOT NULL,
  PRIMARY KEY (id)
);

--;;

CREATE UNIQUE INDEX users_email_index ON users USING btree (email);
