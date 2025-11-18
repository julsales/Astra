# Use a imagem base do OpenJDK 17
FROM eclipse-temurin:17-jre-alpine

# Defina o diretório de trabalho
WORKDIR /app

# Copie o arquivo JAR para o contêiner
COPY apresentacao-backend/target/astra-apresentacao-backend-*.jar app.jar

# Exponha a porta 8080
EXPOSE 8080

# Defina variáveis de ambiente padrão
ENV SPRING_PROFILES_ACTIVE=prod

# Comando para executar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
