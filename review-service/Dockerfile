FROM eclipse-temurin:17-jdk-alpine

ARG JAR_FILE=build/libs/review-service-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} app.jar

EXPOSE 8087

ENTRYPOINT ["java", "-jar", "/app.jar", "--spring.profiles.active=prod"]
