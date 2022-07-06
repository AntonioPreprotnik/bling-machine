INSERT INTO users (id, first_name, last_name, email, is_admin, password_hash)
VALUES (gen_random_uuid(), 'Master', 'Admin', 'admin@vbt.com', TRUE, 'password');
