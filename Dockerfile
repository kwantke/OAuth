FROM gradle:jdk17-alpine as builder
WORKDIR /build

COPY build.gradle settings.gradle /build/
RUN gradle build -x test --parallel > /dev/null 2>&1 || true

COPY . /build
RUN gradle build -x test --parallel

FROM openjdk:17-jdk-slim

WORKDIR /app
COPY --from=builder /build/build/libs/*-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 9090


ENTRYPOINT ["java","-jar","app.jar"]
