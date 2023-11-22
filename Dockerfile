 # Docker Build Stage
FROM openjdk:17-alpine as builder
COPY . /gradle
WORKDIR /gradle
RUN gradle build --no-daemon

FROM openjdk:17-oracle
COPY --from=build /gradle/build/libs/oauth-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=dev","app.jar"]
EXPOSE 8090