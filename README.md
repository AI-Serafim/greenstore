# GreenStore - Интернет-магазин эко-товаров

Веб-приложение интернет-магазина для продажи экологически чистых товаров.

## Технологии

- **Backend**: Java Servlets, JSP, JSTL
- **База данных**: MySQL 8.0
- **Сборка**: Maven
- **Безопасность**: BCrypt для хэширования паролей

## Структура проекта

```
greenstore/
├── database/              # SQL скрипты для БД
│   └── init.sql          # Инициализация базы данных
├── src/main/java/com/greenstore/
│   ├── controller/       # Servlet-контроллеры
│   ├── dao/             # Data Access Object
│   ├── model/           # Модели данных
│   ├── service/         # Бизнес-логика
│   └── util/            # Утилиты
├── src/main/webapp/
│   ├── WEB-INF/
│   │   ├── views/       # JSP страницы
│   │   └── web.xml      # Конфигурация приложения
│   ├── css/             # Стили
│   └── js/              # JavaScript
└── pom.xml              # Maven конфигурация
```

## Установка и запуск

### 1. Требования

- Java 11+
- Maven 3.6+
- MySQL 8.0+
- Tomcat 9+ или другой servlet-контейнер

### 2. Настройка базы данных

```bash
# Подключитесь к MySQL и выполните скрипт
mysql -u root -p < database/init.sql
```

### 3. Конфигурация подключения

Откройте файл `DatabaseConnection.java` и измените параметры подключения:

```java
private static final String URL = "jdbc:mysql://localhost:3306/greenstore_db";
private static final String USERNAME = "root";
private static final String PASSWORD = "ваш_пароль";
```

### 4. Сборка и развертывание

```bash
# Сборка WAR файла
mvn clean package

# Развертывание на Tomcat
# Скопируйте target/greenstore.war в webapps/ томката
```

### 5. Запуск Tomcat

```bash
# Linux/Mac
$CATALINA_HOME/bin/startup.sh

# Windows
%CATALINA_HOME%\bin\startup.bat
```

## Тестовые учетные данные

После выполнения `init.sql` доступны следующие пользователи:

**Обычный пользователь:**
- Email: test@example.com
- Пароль: password123

**Администратор:**
- Email: admin@greenstore.com
- Пароль: password123

## Функциональность

### Для пользователей:
- Регистрация и аутентификация
- Просмотр каталога товаров
- Фильтрация по категориям
- Добавление товаров в корзину
- Оформление заказов
- Просмотр истории заказов

### Архитектура

Приложение использует MVC паттерн:
- **Model**: Классы в пакете `model`
- **View**: JSP страницы в `/WEB-INF/views/`
- **Controller**: Servlets в пакете `controller`

DAO слой обеспечивает работу с базой данных, Service слой содержит бизнес-логику.

## Безопасность

- Пароли хэшируются с помощью BCrypt
- Используется HttpSession для управления сессиями
- Защита от XSS через экранирование вывода в JSP
- HTTP-only cookies для session cookies

## Лицензия

Учебный проект для демонстрации навыков разработки веб-приложений на Java.
