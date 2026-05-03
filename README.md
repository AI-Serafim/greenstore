# GreenStore

Интернет-магазин эко-товаров на базе Java Servlets, JSP, MySQL и Docker.

## Возможности

- Каталог товаров с категориями
- Регистрация и авторизация пользователей (BCrypt)
- Корзина покупок
- Админ-панель для управления товарами
- Полная поддержка UTF-8 (русский язык в БД, интерфейсе и логах)

---

## Быстрый старт (Docker)

Рекомендуемый способ запуска. Гарантирует изолированное окружение и правильные настройки кодировки.

### 1. Предварительная очистка
Если вы ранее запускали проект и сталкивались с проблемами кодировки или входа, обязательно удалите старые тома, чтобы сбросить базу данных до чистого состояния с правильной кодировкой utf8mb4.

```bash
docker compose down -v
```

### 2. Запуск приложения
Соберите образы и запустите контейнеры:

```bash
docker compose up --build -d
```

### 3. Проверка логов
Дождитесь сообщения об успешном старте:

```bash
docker compose logs -f app
```
Ищите строку: `=== GreenStore Ready ===`

### 4. Доступ к приложению
Откройте браузер: http://localhost:8080/greenstore

#### Данные для входа
В базу данных уже загружен тестовый администратор:
- Email: admin@greenstore.com
- Пароль: password123

---

## Локальный запуск (Java + Maven)

Для разработки и отладки без использования Docker.

### Требования
- JDK 17+
- Maven 3.8+
- MySQL 8.0+ (запущен отдельно)

### 1. Настройка базы данных
Убедитесь, что ваша локальная БД создана с кодировкой utf8mb4:

```sql
CREATE DATABASE greenstore CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Выполните скрипт инициализации из папки `database/init.sql`.

### 2. Конфигурация
Проверьте файл конфигурации (например, `src/main/resources/db.properties` или класс `DatabaseConnection`), указав данные вашего локального MySQL:

```properties
db.url=jdbc:mysql://localhost:3306/greenstore?useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=UTF-8&useUnicode=true
db.user=root
db.password=ваш_пароль
```

### 3. Сборка и запуск

```bash
# Сборка проекта
mvn clean package

# Запуск через плагин Tomcat
mvn tomcat9:run

# ИЛИ вручную: скопируйте target/greenstore.war в папку webapps вашего локального Tomcat
```

---

## Структура проекта

```text
greenstore/
├── database/
│   └── init.sql              # Скрипт инициализации БД (UTF-8, utf8mb4)
├── docker-compose.yml        # Конфигурация Docker (App + DB)
├── Dockerfile                # Сборка образа приложения
├── pom.xml                   # Зависимости Maven
├── settings.xml              # Настройки Maven
└── src/
    ├── main/
    │   ├── java/com/greenstore/
    │   │   ├── servlets/     # Контроллеры (Login, Products, Cart)
    │   │   ├── service/      # Бизнес-логика
    │   │   ├── model/        # Сущности (User, Product)
    │   │   └── util/         # Утилиты (DB Connection, Password Hasher)
    │   ├── resources/        # Конфиги (db.properties)
    │   └── webapp/
    │       ├── WEB-INF/
    │       │   └── views/    # JSP страницы
    │       ├── css/          # Стили
    │       └── index.jsp
    └── test/                 # Тесты
```

---

## Решение проблем (FAQ)

### Проблема: Некорректное отображение русского текста ("кракозябры")
**Причина:** Старый том Docker с базой данных, созданной в неправильной кодировке (latin1), или отсутствие параметров кодировки в JDBC URL.
**Решение:**
1. Остановите контейнеры: `docker compose down -v` (флаг `-v` критически важен для удаления тома).
2. Убедитесь, что файл `database/init.sql` сохранен в кодировке UTF-8 без BOM.
3. Запустите заново: `docker compose up --build -d`.

### Проблема: Не работает вход (Неверный логин/пароль)
**Причина:** Хеш пароля в базе данных отличается от ожидаемого BCrypt хеша или был обрезан из-за типа колонки.
**Решение:**
1. Убедитесь, что вы выполнили `docker compose down -v`, чтобы применить свежий `init.sql` с корректным хешем.
2. Проверьте хеш в БД вручную:
   ```bash
   docker exec -it greenstore_db mysql -u greenstore_user -pgreenstore_password greenstore -e "SELECT email, password_hash FROM users WHERE email='admin@greenstore.com';"
   ```
   Хеш должен быть в точности: `$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy`
3. Длина поля `password_hash` в БД должна быть не менее 60 символов (тип VARCHAR(255)).

### Проблема: Ошибка подключения к БД при локальном запуске
**Решение:** Проверьте, что ваш локальный сервер MySQL запущен и параметры подключения в коде или свойствах соответствуют вашему окружению. Убедитесь, что драйвер MySQL подключен в `pom.xml`.

---

## Технологии

- Backend: Java 17, Servlet API 4.0, JSP, JSTL
- Database: MySQL 8.0 (utf8mb4)
- Security: BCrypt
- Build: Maven
- Deployment: Docker, Docker Compose
- Server: Apache Tomcat 9.0
