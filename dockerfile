FROM openjdk:17-jdk-slim

WORKDIR /app

# Copier le fichier JAR
COPY target/medical-dme-rest-service-1.0.0.jar app.jar

# Exposer le port
EXPOSE 8082

# Définir les variables d'environnement
ENV SPRING_PROFILES_ACTIVE=production
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Commande de démarrage
ENTRYPOINT ["java", "-jar", "app.jar"]