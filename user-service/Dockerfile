FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY user-service/pom.xml user-service/pom.xml
COPY notification-service/pom.xml notification-service/pom.xml
RUN mvn -q -pl user-service dependency:go-offline

COPY user-service/src user-service/src
RUN mvn -q -pl user-service package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/user-service/target/user-service-1.0-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
