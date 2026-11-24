# Stage 1: Build the Spring app
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app
COPY dataset-api/pom.xml .
RUN mvn -B dependency:go-offline

COPY dataset-api/src/ /app/src/
COPY dataset/* /app/src/main/resources/dataset/

RUN mvn -B package -DskipTests

# Stage 2: Run the Spring app
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/target/dataset-api.jar /app/dataset-api.jar
EXPOSE 80
CMD ["java", "-jar", "/app/dataset-api.jar", "--spring.profiles.active=prod"]
