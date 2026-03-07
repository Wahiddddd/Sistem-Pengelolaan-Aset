-- Password is 'password123' (BCrypt hashed)
INSERT INTO users (nik, username, password, role, failed_attempts, is_deleted)
VALUES ('1234567890', 'admin_user', '$2a$10$8.730b.n2/qE.vW/3H/Adu3/z6PzP/9/I6E8z9/I6E8z9/I6E8z9/', 'ROLE_ADMIN', 0, FALSE)
ON DUPLICATE KEY UPDATE username=username;
