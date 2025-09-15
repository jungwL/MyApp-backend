# 베이스 이미지로 Java 17 버전을 사용합니다.
FROM eclipse-temurin:21-jdk-jammy

# JAR 파일이 생성될 경로를 변수로 지정합니다.
ARG JAR_FILE=build/libs/*.jar

# JAR 파일을 컨테이너 내부의 app.jar로 복사합니다.
COPY ${JAR_FILE} app.jar

# 애플리케이션 실행 명령어를 설정합니다.
ENTRYPOINT ["java","-jar","/app.jar"]

