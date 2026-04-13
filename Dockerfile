# ── Stage 1: Build ──────────────────────────────────────────
FROM eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /workspace

COPY gradlew ./
COPY gradle ./gradle
COPY settings.gradle.kts build.gradle.kts ./
COPY src ./src

# Build without running tests (tests run in CI)
RUN ./gradlew bootJar --no-daemon -x test

# ── Stage 2: Runtime ─────────────────────────────────────────
FROM eclipse-temurin:17-jre-jammy

RUN groupadd -r appgroup && useradd -r -g appgroup appuser

WORKDIR /app

COPY --from=builder /workspace/build/libs/app.jar app.jar

RUN mkdir -p /app/uploads && chown -R appuser:appgroup /app

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
