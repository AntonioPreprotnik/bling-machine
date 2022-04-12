FROM azul/zulu-openjdk-alpine:17

WORKDIR /

COPY target/xiana-1.2.150-standalone.jar app.jar
COPY entrypoint.sh ./

RUN chmod +x ./entrypoint.sh
ENTRYPOINT ./entrypoint.sh

EXPOSE 3000

CMD trap 'exit' INT
