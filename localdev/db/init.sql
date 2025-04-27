CREATE TABLE sector (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(10) NOT NULL,
    base_price DECIMAL(10,2) NOT NULL,
    max_capacity INT NOT NULL,
    open_hour TIME NOT NULL,
    close_hour TIME NOT NULL,
    duration_limit_minutes INT NOT NULL,
    current_occupancy INT DEFAULT 0,
    is_open BOOLEAN DEFAULT TRUE
);

CREATE TABLE parking_spot (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sector_id BIGINT NOT NULL,
    latitude DECIMAL(10,7) NOT NULL,
    longitude DECIMAL(10,7) NOT NULL,
    FOREIGN KEY (sector_id) REFERENCES sector(id)
);

CREATE TABLE vehicle (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    license_plate VARCHAR(10) NOT NULL UNIQUE,
    entry_time DATETIME NOT NULL,
    exit_time DATETIME,
    parked_time DATETIME,
    spot_id BIGINT,
    sector_id BIGINT,
    current_price DECIMAL(10,2) DEFAULT 0.00,
    FOREIGN KEY (spot_id) REFERENCES parking_spot(id),
    FOREIGN KEY (sector_id) REFERENCES sector(id)
);

CREATE TABLE billing_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    license_plate VARCHAR(10) NOT NULL,
    sector_id BIGINT NOT NULL,
    entry_time DATETIME NOT NULL,
    exit_time DATETIME NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'BRL',
    FOREIGN KEY (sector_id) REFERENCES sector(id)
);