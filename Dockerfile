FROM openjdk:8-jdk-alpine
MAINTAINER sergioadsf
#USER sergio

ARG url_csv=/bck/files
ARG url_propostas=/bck/arquivos_para_indexar
WORKDIR /app
COPY ./target/scd.jar /app
RUN mkdir /bck
EXPOSE 8082
VOLUME /bck:/home/sergio/Downloads
ENTRYPOINT java -jar scd.jar
