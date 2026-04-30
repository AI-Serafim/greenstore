FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# Копируем файлы сборки и скачиваем зависимости с повторными попытками
COPY pom.xml .
RUN mvn dependency:go-offline -B -Dmaven.wagon.http.retryHandler.count=3 || mvn dependency:go-offline -B

# Копируем исходный код и собираем проект
COPY src ./src
RUN mvn clean package -DskipTests -B

# Финальный образ с Tomcat
FROM tomcat:10.1-jdk17

# Удаляем стандартное приложение ROOT (опционально)
RUN rm -rf /usr/local/tomcat/webapps/ROOT

# Копируем WAR файл из стадии сборки
COPY --from=builder /app/target/greenstore.war /usr/local/tomcat/webapps/greenstore.war

# Открываем порт
EXPOSE 8080

# Запуск Tomcat
CMD ["catalina.sh", "run"]
