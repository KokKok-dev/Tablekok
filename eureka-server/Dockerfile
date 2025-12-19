FROM eclipse-temurin:17-jdk-alpine

ARG JAR_FILE=build/libs/eureka-server-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} app.jar

ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 19090

ENTRYPOINT ["java", "-jar", "/app.jar"]
