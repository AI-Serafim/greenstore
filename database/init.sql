-- Инициализация базы данных для интернет-магазина "GreenStore"
-- Создает базу данных и все необходимые таблицы

-- Создание базы данных
CREATE DATABASE IF NOT EXISTS greenstore_db;
USE greenstore_db;

-- Таблица пользователей
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    role ENUM('USER', 'ADMIN') DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Таблица категорий товаров
CREATE TABLE categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    image_url VARCHAR(255),
    sort_order INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Таблица товаров
CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    category_id INT NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    stock_quantity INT DEFAULT 0,
    image_url VARCHAR(255),
    is_available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Таблица корзины покупок
CREATE TABLE cart (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_product (user_id, product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Таблица заказов
CREATE TABLE orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    order_number VARCHAR(50) NOT NULL UNIQUE,
    status ENUM('PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED') DEFAULT 'PENDING',
    total_amount DECIMAL(10, 2) NOT NULL,
    delivery_address TEXT NOT NULL,
    contact_phone VARCHAR(20) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Таблица элементов заказа
CREATE TABLE order_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    product_name VARCHAR(200) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Индексы для оптимизации запросов
CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_products_available ON products(is_available);
CREATE INDEX idx_cart_user ON cart(user_id);
CREATE INDEX idx_orders_user ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_order_items_order ON order_items(order_id);

-- Добавление тестовых данных

-- Категории товаров
INSERT INTO categories (name, description, image_url, sort_order) VALUES
('Эко-продукты питания', 'Натуральные и органические продукты питания', '/images/categories/food.jpg', 1),
('Бытовая химия', 'Экологически чистые средства для дома', '/images/categories/cleaning.jpg', 2),
('Косметика и уход', 'Натуральная косметика и средства гигиены', '/images/categories/cosmetics.jpg', 3),
('Товары для дома', 'Эко-товары для уютного дома', '/images/categories/home.jpg', 4),
('Одежда и текстиль', 'Одежда из натуральных материалов', '/images/categories/clothing.jpg', 5);

-- Товары
INSERT INTO products (category_id, name, description, price, stock_quantity, image_url) VALUES
(1, 'Органический мёд', 'Натуральный цветочный мёд с пасеки', 450.00, 50, '/images/products/honey.jpg'),
(1, 'Чиа семена', 'Органические семена чиа, 500г', 320.00, 100, '/images/products/chia.jpg'),
(1, 'Зелёный чай матча', 'Японский зелёный чай матча, 100г', 890.00, 30, '/images/products/matcha.jpg'),
(2, 'Эко-средство для посуды', 'Концентрированное средство, 500мл', 280.00, 75, '/images/products/dish soap.jpg'),
(2, 'Универсальный очиститель', 'Натуральный очиститель для поверхностей', 350.00, 60, '/images/products/cleaner.jpg'),
(3, 'Шампунь без сульфатов', 'Натуральный шампунь для всех типов волос', 520.00, 40, '/images/products/shampoo.jpg'),
(3, 'Крем для лица', 'Увлажняющий крем с органическими маслами', 780.00, 35, '/images/products/face cream.jpg'),
(4, 'Бамбуковые зубные щётки', 'Набор из 4 штук', 290.00, 80, '/images/products/toothbrush.jpg'),
(4, 'Многоразовые сумки', 'Набор эко-сумок для покупок, 3 шт', 450.00, 90, '/images/products/bags.jpg'),
(5, 'Хлопковая футболка', 'Футболка из 100% органического хлопка', 1200.00, 25, '/images/products/tshirt.jpg');

-- Тестовый пользователь (пароль: password123)
INSERT INTO users (email, password_hash, first_name, last_name, phone, address, role) VALUES
('test@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Иван', 'Петров', '+7 (999) 123-45-67', 'г. Москва, ул. Примерная, д. 1', 'USER'),
('admin@greenstore.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Админ', 'Админов', '+7 (999) 000-00-00', 'г. Москва', 'ADMIN');
