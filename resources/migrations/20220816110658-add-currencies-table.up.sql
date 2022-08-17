CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
--;;
CREATE TABLE currencies
(
    id uuid NOT NULL DEFAULT uuid_generate_v4() PRIMARY KEY,
    currency_name   TEXT NOT NULL,
    exchange_rate   FLOAT NOT NULL,
    creation_date   TEXT NOT NULL
);
--;;
CREATE UNIQUE INDEX name_to_date_index ON currencies (currency_name, creation_date);
