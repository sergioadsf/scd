FROM openjdk:8-jdk-alpine
MAINTAINER sergioadsf
#USER sergio

ARG url_csv=/home/sergio/Downloads/files
ARG url_propostas=/home/sergio/Downloads/files2
WORKDIR /app
COPY ./target/scd.jar /app
RUN mkdir /bck
EXPOSE 8082
#VOLUME /home/sergio/Downloads/files:/bck
ENTRYPOINT java -jar scd.jar
