CREATE DATABASE user_management;
USE user_management;
CREATE TABLE users (
	`user_id` BINARY(16) DEFAULT(UUID_TO_BIN(UUID())) PRIMARY KEY,
	`username` VARCHAR(20) NOT NULL,-- UNIQUE  
	`password` VARCHAR(65) NOT NULL,
	`email` VARCHAR(40) NOT NULL,
    INDEX username (username)
);

CREATE DATABASE product_management;
USE product_management;
CREATE TABLE products (
	`product_id` BINARY(16) DEFAULT(UUID_TO_BIN(UUID())) PRIMARY KEY,
	`name` VARCHAR(20) NOT NULL, -- UNIQUE 
	`description` VARCHAR(100),
	`price` INT UNSIGNED,
    `stock` INT UNSIGNED DEFAULT(0),
    `img_location` VARCHAR(100)
);

CREATE DATABASE order_management;
USE order_management;
CREATE TABLE orders (
	`order_id` BINARY(16) DEFAULT(UUID_TO_BIN(UUID())) PRIMARY KEY,
	`order_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	`user_id` BINARY(16) NOT NULL-- REFERENCES users.user_id
);
CREATE TABLE ordered_items (
	`order_id` BINARY(16) NOT NULL,-- REFERENCES　orders.order_id
	`product_id` BINARY(16) NOT NULL,-- REFERENCES products.product_id
	`quantity` INT NOT NULL,
    INDEX order_id (order_id)
);

-- INSERT INTO users (username, password) VALUES ("abce", 123);
-- ALTER TABLE users MODIFY COLUMN name varchar(20);
-- ALTER TABLE users ADD COLUMN name varchar(20) not null;
-- ALTER TABLE price DROP INDEX aptId;