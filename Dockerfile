FROM openjdk:17-jdk-slim
LABEL authors="OHS"

#WORKDIR /app

#COPY build/libs/app.jar app.jar
#COPY build/libs/$JAR_FILE app.jar
COPY app.jar /
ENTRYPOINT ["java", "-jar", "app.jar"]