#FROM ubuntu:latest
#LABEL authors="cuhun"
#
#ENTRYPOINT ["top", "-b"]
FROM openjdk:21-jdk
VOLUME /tmp
ARG JAR_FILE=build/libs/sketch-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

