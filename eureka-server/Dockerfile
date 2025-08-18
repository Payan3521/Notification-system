FROM openjdk:17-jdk-slim

LABEL maintainer="pablobedoya3521@gmail.com"
LABEL version="1.0.0"
LABEL description="Eureka Server para sistema de notificaciones"

WORKDIR /app

COPY target/eureka-server-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8761

ENV JAVA_OPTS="-Xmx512m -Xms256m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]