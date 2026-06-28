FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY user-service/pom.xml user-service/pom.xml
COPY notification-service/pom.xml notification-service/pom.xml
COPY config-server/pom.xml config-server/pom.xml
COPY discovery-server/pom.xml discovery-server/pom.xml
COPY api-gateway/pom.xml api-gateway/pom.xml
RUN mvn -q -pl discovery-server dependency:go-offline

COPY discovery-server/src discovery-server/src
RUN mvn -q -pl discovery-server package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/discovery-server/target/discovery-server-1.0-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
