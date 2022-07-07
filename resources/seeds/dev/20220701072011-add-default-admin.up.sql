INSERT INTO users (id, first_name, last_name, email, is_admin, password_hash)
VALUES (gen_random_uuid(), 'Master', 'Admin', 'admin@vbt.com', TRUE, 'bcrypt+sha512$eb71aa44143bfbc9daeebd5c62bea513$12$a966f3d366d8ad16a14182fcfad1f8eccc80e59a2fb49bdf');
