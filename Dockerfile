# 1. Java 17 베이스 이미지 사용 (가볍고 빠른 alpine 버전)
FROM eclipse-temurin:17-jdk-alpine

# 2. 작업 디렉토리 설정
VOLUME /tmp

# 3. 빌드된 JAR 파일을 이미지 내부로 복사
# (Gradle 빌드 시 build/libs/ 폴더에 jar가 생성됨)
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# 4. 컨테이너 실행 시 애플리케이션 실행
ENTRYPOINT ["java","-jar","/app.jar"]