# Build stage
FROM gradle:8.10-jdk21 AS build

# Set the working directory
WORKDIR /app

# Copy the Gradle wrapper and build files
COPY gradle /app/gradle
COPY gradlew /app/gradlew
COPY build.gradle.kts /app/build.gradle.kts
COPY settings.gradle.kts /app/settings.gradle.kts

# Copy the source code
COPY src /app/src

# Build the application
RUN #./gradlew shadowJar
RUN ./gradlew clean bootJar -x test

# Run stage
FROM eclipse-temurin:21-jre-alpine

# Set the working directory
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/build/libs/product-service-0.0.1-SNAPSHOT.jar /app/app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]