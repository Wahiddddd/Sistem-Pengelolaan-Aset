CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nik VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    failed_attempts INT DEFAULT 0,
    locked_until DATETIME,
    is_deleted BOOLEAN DEFAULT FALSE
);

CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    is_deleted BOOLEAN DEFAULT FALSE
);

CREATE TABLE assets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    serial_number VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    image_path VARCHAR(255),
    purchase_date DATE NOT NULL,
    maintenance_frequency INT NOT NULL,
    status VARCHAR(50),
    next_maintenance_date DATE,
    is_deleted BOOLEAN DEFAULT FALSE,
    category_id BIGINT,
    CONSTRAINT fk_asset_category FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE maintenance_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    start_time DATETIME,
    end_time DATETIME,
    description TEXT,
    cost DECIMAL(19, 2),
    photo_before VARCHAR(255),
    photo_after VARCHAR(255),
    is_deleted BOOLEAN DEFAULT FALSE,
    asset_id BIGINT,
    user_id BIGINT,
    CONSTRAINT fk_log_asset FOREIGN KEY (asset_id) REFERENCES assets(id),
    CONSTRAINT fk_log_user FOREIGN KEY (user_id) REFERENCES users(id)
);
