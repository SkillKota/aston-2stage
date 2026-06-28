FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY user-service/pom.xml user-service/pom.xml
COPY notification-service/pom.xml notification-service/pom.xml
COPY config-server/pom.xml config-server/pom.xml
COPY discovery-server/pom.xml discovery-server/pom.xml
COPY api-gateway/pom.xml api-gateway/pom.xml
RUN mvn -q -pl config-server dependency:go-offline

COPY config-server/src config-server/src
RUN mvn -q -pl config-server package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/config-server/target/config-server-1.0-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
