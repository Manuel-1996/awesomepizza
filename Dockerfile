# Dockerfile principale per produzione
FROM maven:3.9.10-eclipse-temurin-17 as builder

WORKDIR /app

# Copia i file di configurazione Maven
COPY pom.xml ./
# COPY mvnw ./
# COPY mvnw.cmd ./
# COPY .mvn ./.mvn

# Scarica le dipendenze (layer cache)
RUN mvn dependency:go-offline -B

# Copia il codice sorgente
COPY src ./src

# Compila l'applicazione
RUN mvn clean package -DskipTests

# Stage di produzione
FROM eclipse-temurin:17-jre-jammy as production

WORKDIR /app

# Installa curl per health checks (opzionale)
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Copia il JAR compilato dal builder stage
COPY --from=builder /app/target/*.jar app.jar

# Crea un utente non-root per sicurezza
RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring:spring

# Esposizione della porta
EXPOSE ${PORT:-8080}

# Avvio dell'applicazione
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
