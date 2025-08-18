# ✅ Usar imagen base oficial de OpenJDK 17
FROM openjdk:17-jdk-slim

# ✅ Etiquetas para metadata
LABEL maintainer="pablobedoya3521@gmail.com"
LABEL version="1.0.0"
LABEL description="Microservicio despachador de notificaciones"

# ✅ Crear directorio de trabajo
WORKDIR /app

# ✅ Copiar el JAR del proyecto
COPY target/microservice-notification-dispatcher-0.0.1-SNAPSHOT.jar app.jar

# ✅ Exponer puerto
EXPOSE 8082

# ✅ Variables de entorno para JVM
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# ✅ Comando de ejecución
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]