# 빌드 스테이지
FROM gradle:8.7-jdk17 AS build
WORKDIR /app

# apt-get 업데이트 및 dos2unix 설치 (Windows 라인 엔딩 문제 해결용)
RUN apt-get update && apt-get install -y dos2unix

# Gradle Wrapper 및 프로젝트 파일 복사
COPY gradlew gradlew
COPY gradlew.bat gradlew.bat
COPY gradle gradle
COPY . .

# gradlew 개행문자 변환 및 권한 부여
RUN dos2unix gradlew
RUN chmod +x ./gradlew

# Gradle 빌드 (테스트 생략)
RUN ./gradlew build -x test

# 실행 이미지 생성
FROM openjdk:17-slim
WORKDIR /app

# 빌드 결과 JAR 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 출력 디렉토리 생성
RUN mkdir -p /shared-output

# 환경 변수 설정
ENV SPRING_DATASOURCE_URL="jdbc:mysql://mysql:3306/skaxis?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
ENV SPRING_DATASOURCE_USERNAME="skaxis"
ENV SPRING_DATASOURCE_PASSWORD="skaxispassword"
ENV SPRING_JPA_HIBERNATE_DDL_AUTO="update"
ENV FASTAPI_URL="http://fastapi:8000/api/v1"
ENV OUTPUT_DIR="/shared-output"

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]