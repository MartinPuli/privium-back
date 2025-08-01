# syntax=docker/dockerfile:1

# -- Build stage ------------------------------------------------------------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q package

# -- Runtime stage ----------------------------------------------------------
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/marketplace-backend-0.0.1-SNAPSHOT.jar app.jar
ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
