# 1. 베이스 이미지: Eclipse Temurin Java 17 (경량 Alpine 리눅스)
FROM eclipse-temurin:17-jdk-alpine

# 2. 타임존 설정: 한국 시간 (설정 안 하면 UTC, 9시간 차이남)
ENV TZ=Asia/Seoul

# 3. 작업 디렉토리 설정: 컨테이너 내부에서 /app 폴더 사용
WORKDIR /app

# 4. JAR 파일 복사: 빌드된 JAR을 컨테이너로 복사
COPY build/libs/*.jar app.jar

# 5. 포트 노출: user-service는 8081 포트 사용 (문서화 용도)
EXPOSE 8081

# 6. 애플리케이션 실행: JVM 타임존 설정 + prod 프로필로 실행
ENTRYPOINT ["java", "-Duser.timezone=${TZ}", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
