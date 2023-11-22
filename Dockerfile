 # Docker Build Stage
FROM openjdk:17-alpine

ARG JAR_FILE=/build/libs/oauth-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} /oauth.jar

ENTRYPOINT ["java","-jar","-Dspring.profiles.active=dev","/oauth.jar"]
