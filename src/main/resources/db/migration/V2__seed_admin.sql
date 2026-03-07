-- Password is 'password123' (BCrypt hashed)
INSERT INTO users (nik, username, password, role, failed_attempts, is_deleted)
VALUES ('1234567890', 'admin_user', '$2a$10$kypbnGGCpJ7UQlysnqzJG.6H.dUewn7UPVWA3Ip.E.8U4jlVnFNnu', 'ROLE_ADMIN', 0, FALSE)
ON DUPLICATE KEY UPDATE password=VALUES(password);
