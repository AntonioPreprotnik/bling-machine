CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
--;;
CREATE EXTENSION IF NOT EXISTS citext;
--;;
CREATE TABLE users (
  id uuid NOT NULL DEFAULT uuid_generate_v4() PRIMARY KEY,
  first_name character varying(255) NOT NULL,
  last_name character varying(255) NOT NULL,
  email citext NOT NULL,
  is_admin BOOLEAN DEFAULT FALSE,
  password_hash VARCHAR(255) NOT NULL,
  inserted_at timestamp without time zone DEFAULT NOW(),
  updated_at timestamp without time zone DEFAULT NOW()
);

--;;

CREATE UNIQUE INDEX users_email_index ON users USING btree (email);
