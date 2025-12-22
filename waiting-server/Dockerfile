FROM eclipse-temurin:17-jdk-jammy

ENV TZ=Asia/Seoul

WORKDIR /app

COPY build/libs/*.jar app.jar

ENTRYPOINT ["java", "-Duser.timezone=${TZ}", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
