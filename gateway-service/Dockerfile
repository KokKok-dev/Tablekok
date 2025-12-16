FROM eclipse-temurin:17-jdk

ARG JAR_FILE=build/libs/gateway-service-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} app.jar

EXPOSE 19091

ENTRYPOINT ["java", "-jar", "app.jar"]
