# Java 21의 OpenJDK 이미지 사용
FROM openjdk:21-jdk-slim

# 컨테이너 내부의 작업 디렉토리 설정
WORKDIR /app

# Gradle 빌드로 생성된 JAR 파일을 컨테이너로 복사
COPY build/libs/find-it-application.jar app.jar

# 애플리케이션에서 사용하는 포트를 컨테이너에서 노출
EXPOSE 8080

# JAR 파일을 실행하는 명령어 설정
ENTRYPOINT ["java", "-jar", "app.jar"]
