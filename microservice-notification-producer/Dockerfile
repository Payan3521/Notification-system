FROM openjdk:17-jdk-slim

LABEL maintaine="pablobedoya3521@gmail.com"
LABEL version="1.0.0"
LABEL description="Microservicio productor de notificaciones"

WORKDIR /app

COPY target/microservice-notification-producer-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8081

ENV JAVA_OPTS="-Xmx512m -Xms256m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]