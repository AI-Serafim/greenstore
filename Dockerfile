FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# Копируем settings.xml с зеркалом Google для стабильности
COPY settings.xml /root/.m2/settings.xml

# Копируем файлы сборки и скачиваем зависимости
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Копируем исходный код и собираем проект
COPY src ./src
RUN mvn clean package -DskipTests -B

# Финальный образ с Tomcat
FROM tomcat:9.0-jdk17

ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8
ENV JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8"
ENV JAVA_OPTS="-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8"
ENV CATALINA_OPTS="-Dfile.encoding=UTF-8"

# Удаляем стандартное приложение ROOT (опционально)
RUN rm -rf /usr/local/tomcat/webapps/ROOT

# Копируем WAR файл из стадии сборки
COPY --from=builder /app/target/greenstore.war /usr/local/tomcat/webapps/greenstore.war

# Открываем порт
EXPOSE 8080

# Запуск Tomcat
CMD ["sh", "-c", "catalina.sh run"]
