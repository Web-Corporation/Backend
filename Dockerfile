#FROM ubuntu:latest
#LABEL authors="cuhun"
#
#ENTRYPOINT ["top", "-b"]
FROM openjdk:21-jdk
VOLUME /tmp
ARG JAR_FILE=build/libs/sketch-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ADD https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh
ENTRYPOINT ["./wait-for-it.sh", "db-service:3306", "--", "java", "-jar", "/app.jar"]

