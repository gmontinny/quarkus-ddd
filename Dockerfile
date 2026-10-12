# =============================================================================
# Stage 1: Build
# =============================================================================
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy gradle wrapper and config first (layer cache)
COPY gradlew gradlew.bat ./
COPY gradle/ gradle/
COPY gradle.properties settings.gradle build.gradle ./

# Download dependencies (cached layer if build files unchanged)
RUN ./gradlew dependencies --no-daemon -q 2>/dev/null || true

# Copy source and data
COPY src/ src/
COPY data/ data/

# Build the application (skip tests in image build)
RUN ./gradlew build -x test --no-daemon -Dquarkus.package.jar.type=fast-jar

# =============================================================================
# Stage 2: Runtime
# =============================================================================
FROM eclipse-temurin:21-jre-alpine AS runtime

LABEL maintainer="olist-team"
LABEL app="quarkus-ddd"
LABEL version="1.0.0"

# Create non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /deployments

# Copy built artifacts from builder stage (layered for cache efficiency)
COPY --from=builder --chown=appuser:appgroup /app/build/quarkus-app/lib/           ./lib/
COPY --from=builder --chown=appuser:appgroup /app/build/quarkus-app/*.jar          ./
COPY --from=builder --chown=appuser:appgroup /app/build/quarkus-app/app/           ./app/
COPY --from=builder --chown=appuser:appgroup /app/build/quarkus-app/quarkus/       ./quarkus/

# Copy CSV data files for Liquibase import
COPY --from=builder --chown=appuser:appgroup /app/data/                            ./data/

USER appuser

EXPOSE 8081

# JVM tuning for containers
ENV JAVA_OPTS="\
  -Dquarkus.http.host=0.0.0.0 \
  -Djava.util.logging.manager=org.jboss.logmanager.LogManager \
  -XX:+UseContainerSupport \
  -XX:MaxRAMPercentage=75.0 \
  -XX:+OptimizeStringConcat \
  -XX:+UseG1GC \
  -Dfile.encoding=UTF-8"

HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
  CMD wget -qO- http://localhost:8081/api/v1/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /deployments/quarkus-run.jar"]
