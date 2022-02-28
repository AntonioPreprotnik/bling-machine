FROM openjdk:8u181-alpine3.8

WORKDIR /

COPY target/app.jar app.jar
COPY entrypoint.sh ./

RUN chmod +x ./entrypoint.sh
ENTRYPOINT ./entrypoint.sh

EXPOSE 3000

CMD trap 'exit' INT