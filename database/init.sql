SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET COLLATION_CONNECTION = 'utf8mb4_unicode_ci';

DROP DATABASE IF EXISTS greenstore;
CREATE DATABASE greenstore CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE greenstore;

-- Таблица пользователей
CREATE TABLE IF NOT EXISTS users (
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

-- Таблица категорий
CREATE TABLE IF NOT EXISTS categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    image_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Таблица товаров
CREATE TABLE IF NOT EXISTS products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    category_id INT,
    image_url VARCHAR(255),
    stock_quantity INT DEFAULT 0,
    is_available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Таблица заказов
CREATE TABLE IF NOT EXISTS orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    status ENUM('NEW', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED') DEFAULT 'NEW',
    shipping_address TEXT NOT NULL,
    contact_phone VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Таблица элементов заказа
CREATE TABLE IF NOT EXISTS order_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Вставка администратора
-- Хеш для пароля "password123"
INSERT INTO users (email, password_hash, first_name, last_name, role) VALUES
('admin@greenstore.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Админ', 'Иванов', 'ADMIN');

-- Вставка категорий (используем явное приведение кодировки)
INSERT INTO categories (name, description) VALUES
(CONVERT('Эко-продукты питания' USING utf8mb4), 'Натуральные и органические продукты'),
(CONVERT('Бытовая химия' USING utf8mb4), 'Экологически чистые моющие средства'),
(CONVERT('Косметика и уход' USING utf8mb4), 'Натуральная косметика без химии'),
(CONVERT('Товары для дома' USING utf8mb4), 'Эко-товары для быта'),
(CONVERT('Одежда и текстиль' USING utf8mb4), 'Одежда из натуральных материалов');

-- Вставка товаров
INSERT INTO products (name, description, price, category_id, stock_quantity) VALUES
(CONVERT('Органический мёд' USING utf8mb4), 'Натуральный горный мёд', 850.00, 1, 50),
(CONVERT('Чиа семена' USING utf8mb4), 'Семена чиа, 500г', 450.00, 1, 100),
(CONVERT('Зелёный чай матча' USING utf8mb4), 'Японский чай матча, 100г', 1200.00, 1, 30),
(CONVERT('Эко-средство для посуды' USING utf8mb4), 'Гель для мытья посуды, 500мл', 350.00, 2, 200),
(CONVERT('Универсальный очиститель' USING utf8mb4), 'Спрей для уборки, 750мл', 420.00, 2, 150),
(CONVERT('Шампунь без сульфатов' USING utf8mb4), 'Натуральный шампунь, 250мл', 650.00, 3, 80),
(CONVERT('Крем для рук' USING utf8mb4), 'Увлажняющий крем с маслом ши', 380.00, 3, 120),
(CONVERT('Бамбуковые полотенца' USING utf8mb4), 'Набор из 3 полотенец', 1500.00, 4, 40),
(CONVERT('Восковые салфетки' USING utf8mb4), 'Многоразовая упаковка, набор', 900.00, 4, 60),
(CONVERT('Хлопковая сумка-шоппер' USING utf8mb4), 'Эко-сумка для покупок', 250.00, 5, 300);
