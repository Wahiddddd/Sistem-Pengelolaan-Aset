-- 1. Tabel Categories
CREATE TABLE categories (
id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
name VARCHAR(255) NOT NULL,
is_deleted BOOLEAN DEFAULT FALSE
);



-- 2. Tabel Users
CREATE TABLE users (
id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
nik VARCHAR(50) UNIQUE NOT NULL,
username VARCHAR(100) UNIQUE NOT NULL,
password VARCHAR(255) NOT NULL,
role VARCHAR(20) NOT NULL,
failed_attempts INT DEFAULT 0,
locked_until DATETIME,
is_deleted BOOLEAN DEFAULT FALSE
);



-- 3. Tabel Assets
CREATE TABLE assets (
id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
serial_number VARCHAR(100) UNIQUE NOT NULL,
name VARCHAR(255) NOT NULL,
image_path VARCHAR(255),
purchase_date DATE,
maintenance_frequency INT,
status VARCHAR(50),
next_maintenance_date DATE,
category_id BIGINT,
is_deleted BOOLEAN DEFAULT FALSE,
CONSTRAINT fk_asset_category FOREIGN KEY (category_id) REFERENCES categories(id)
);



-- 4. Tabel Maintenance Logs
CREATE TABLE maintenance_logs (
id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
start_time DATETIME,
end_time DATETIME,
description TEXT,
cost DECIMAL(19,2),
photo_before VARCHAR(255),
photo_after VARCHAR(255),
asset_id BIGINT,
user_id BIGINT,
is_deleted BOOLEAN DEFAULT FALSE,
CONSTRAINT fk_log_asset FOREIGN KEY (asset_id) REFERENCES assets(id),
CONSTRAINT fk_log_user FOREIGN KEY (user_id) REFERENCES users(id)
);