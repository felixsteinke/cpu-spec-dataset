# Stage 1: Build the Spring app
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY dataset-api/pom.xml .
RUN mvn dependency:go-offline
COPY dataset-api/src/ /app/src/
RUN mvn package -DskipTests

# Stage 2: Run the Spring app
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/dataset-api-1.0.jar /app/app.jar
EXPOSE 80
CMD ["java", "-jar", "/app/app.jar", "--spring.profiles.active=prod"]
