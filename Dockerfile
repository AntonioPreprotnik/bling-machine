FROM openjdk:17-jdk-alpine

WORKDIR /

COPY target/app.jar app.jar
COPY entrypoint.sh ./

RUN chmod +x ./entrypoint.sh
ENTRYPOINT ./entrypoint.sh

EXPOSE 3000

CMD trap 'exit' INT
