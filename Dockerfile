# Base image with specific JDK version for reproducibility
FROM openjdk:17-jdk-slim

# Label the image
LABEL authors="OHS"

# Set the working directory to /app
WORKDIR /app

# Copy the application JAR file into the working directory
COPY build/libs/app.jar app.jar

# Set the entrypoint to launch the Java application
ENTRYPOINT ["java", "-jar", "app.jar"]