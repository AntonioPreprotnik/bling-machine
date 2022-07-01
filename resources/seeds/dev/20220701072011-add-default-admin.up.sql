INSERT INTO users (id, email, password_hash, first_name, last_name, zip, is_admin)
VALUES (gen_random_uuid(), 'admin@vbt.com', 'password', 'Master', 'Admin', '10000', TRUE);
