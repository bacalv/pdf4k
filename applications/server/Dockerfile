FROM openjdk:21-ea-1-slim
EXPOSE 8080
RUN mkdir /app
COPY build/distributions/server.zip /app/server.zip
WORKDIR /app
RUN jar -xf server.zip
RUN rm server.zip
WORKDIR /
COPY scripts/run.sh /app/run.sh
RUN chmod +x /app/run.sh
ENTRYPOINT ["/bin/sh", "/app/run.sh"]