# ─── Etapa 1: Build ──────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build

# Copiamos primero el pom.xml para aprovechar la caché de capas Docker
# Si el código cambia pero el pom.xml no, no se re-descargan dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Ahora copiamos el código fuente y compilamos
COPY src ./src
RUN mvn clean package -DskipTests -q

# ─── Etapa 2: Runtime ─────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Usuario sin privilegios (buena práctica de seguridad — principio de menor privilegio)
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copiamos solo el JAR generado — imagen mínima, sin Maven ni código fuente
COPY --from=builder /build/target/auth-service-*.jar app.jar

# Exponemos el puerto del servicio
EXPOSE 8081

# Virtual Threads habilitados por defecto en Java 21
# -XX:+UseVirtualThreads activa el scheduler de threads virtuales para Tomcat
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
