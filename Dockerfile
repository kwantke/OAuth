FROM gradle:jdk17-alpine as builder
WORKDIR /build

# 그래들 파일이 변경되었을 때만 새롭게 의존패키지 다운로드 받게함.
COPY build.gradle settings.gradle /build/
RUN gradle build -x test --parallel --continue > /dev/null 2>&1 || true

# 빌더 이미지에서 애플리케이션 빌드
COPY . /build
RUN gradle build -x test --parallel

# APP
FROM openjdk:17-oracle
WORKDIR /app

# 빌더 이미지에서 jar 파일만 복사
# COPY --from=builder /build/build/libs/*-SNAPSHOT.jar ./app.jar
COPY --from=builder /build/build/libs/oauth-0.0.1-SNAPSHOT.jar .

EXPOSE 9090

# root 대신 nobody 권한으로 실행
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=dev","oauth-0.0.1-SNAPSHOT.jar"]
#ENTRYPOINT [   "java",   "-jar",   "-Djava.security.egd=file:/dev/./urandom",   "-Dsun.net.inetaddr.ttl=0",   "oauth-0.0.1-SNAPSHOT.jar"]


