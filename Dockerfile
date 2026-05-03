# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app
COPY settings.xml /root/.m2/settings.xml
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime
FROM tomcat:9.0-jdk17

# Кодировка UTF-8
ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8
ENV JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8 -Dclient.encoding.override=UTF-8 -Duser.language=ru -Duser.country=RU"
ENV CATALINA_OPTS="-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8 -Dclient.encoding.override=UTF-8 -Dorg.apache.jasper.compiler.Parser.STRICT_QUOTE_ESCAPING=false"

# Удалить стандартное приложение
RUN rm -rf /usr/local/tomcat/webapps/ROOT

# Настроить кодировку в server.xml Tomcat
RUN sed -i 's|<Connector port="8080" protocol="HTTP/1.1"|<Connector port="8080" protocol="HTTP/1.1" URIEncoding="UTF-8" useBodyEncodingForURI="true"|' /usr/local/tomcat/conf/server.xml

# Развернуть наше приложение
COPY --from=builder /app/target/greenstore.war /usr/local/tomcat/webapps/greenstore.war

EXPOSE 8080

CMD ["catalina.sh", "run"]