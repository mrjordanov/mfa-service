FROM openjdk:17-jdk-slim

LABEL authors="myordanov"

# Set the working directory in Docker
WORKDIR /usr/app

# Copy the application's jar file into the Docker image
COPY ./build/libs/*.jar  mfa-service-0.0.1-SNAPSHOT.jar

# Run the jar file
ENTRYPOINT ["java","-jar","mfa-service-0.0.1-SNAPSHOT.jar"]