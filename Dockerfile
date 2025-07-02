# 1. 베이스 이미지 (Java 17)
FROM eclipse-temurin:17-jdk-alpine

# 2. 작업 디렉토리 생성
WORKDIR /app

# 3. 로컬 빌드 아티팩트 복사
COPY build/libs/*.jar app.jar

# 4. JAR 실행
ENTRYPOINT ["java","-jar","/app/app.jar"]
