CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
--;;
CREATE TABLE currencies
(
    id uuid NOT NULL DEFAULT uuid_generate_v4() PRIMARY KEY,
    currency_name   TEXT NOT NULL,
    exchange_rate   INT NOT NULL,
    created_at TIMESTAMP NOT NULL
);
--;;
CREATE TRIGGER update_currencies_updated_at
    BEFORE UPDATE
    ON currencies
    FOR EACH ROW
EXECUTE PROCEDURE update_updated_at_column();
