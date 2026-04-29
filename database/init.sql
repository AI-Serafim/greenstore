CREATE DATABASE IF NOT EXISTS online_shop;
USE online_shop;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role ENUM('USER') DEFAULT 'USER'
);

CREATE TABLE categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    category_id INT,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE cart (
    user_id INT,
    product_id INT,
    quantity INT,
    PRIMARY KEY (user_id, product_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('PENDING', 'COMPLETED', 'CANCELLED') DEFAULT 'PENDING',
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE order_items (
    order_id INT,
    product_id INT,
    quantity INT,
    price DECIMAL(10, 2),
    PRIMARY KEY (order_id, product_id),
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Тестовые данные
INSERT INTO categories (name) VALUES 
('Электроника'),
('Одежда'),
('Книги'),
('Спорт');

INSERT INTO products (name, description, price, category_id) VALUES 
('Смартфон', 'Современный смартфон с отличными характеристиками', 25000.00, 1),
('Ноутбук', 'Мощный ноутбук для работы и игр', 50000.00, 1),
('Футболка', 'Хлопковая футболка', 1500.00, 2),
('Джинсы', 'Классические джинсы', 3000.00, 2),
('Java для начинающих', 'Учебник по программированию', 1200.00, 3),
('Мяч футбольный', 'Профессиональный футбольный мяч', 2500.00, 4);
