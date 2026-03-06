# ==================== Build ====================
FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app

# Copiar arquivos do Gradle primeiro (cache de dependências)
COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle .
COPY settings.gradle .
COPY gradle.properties .

# Dar permissão de execução ao gradlew
RUN chmod +x gradlew

# Baixar dependências (camada cacheada)
RUN ./gradlew dependencies --no-daemon || true

# Copiar código-fonte
COPY src/ src/

# Build do JAR (sem rodar testes)
RUN ./gradlew clean bootJar --no-daemon -x test

# ==================== Runtime ====================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Criar usuário não-root
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copiar JAR do estágio de build
COPY --from=build /app/build/libs/*.jar app.jar

# Trocar para usuário não-root
USER appuser

# Expor porta da aplicação
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

# Executar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]

