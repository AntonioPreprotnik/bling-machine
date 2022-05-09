#FROM azul/zulu-openjdk-alpine:17 - for apple M1 processor

FROM openjdk:17-jdk-alpine
WORKDIR /

COPY target/xiana-standalone.jar app.jar
COPY entrypoint.sh ./

RUN chmod +x ./entrypoint.sh
ENTRYPOINT ./entrypoint.sh

EXPOSE 3000

CMD trap 'exit' INT
