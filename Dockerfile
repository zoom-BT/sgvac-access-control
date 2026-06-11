# ---- Build stage ----
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Gradle wrapper + build scripts first (better layer caching)
COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew

# Application sources
COPY src ./src

# Build only the executable boot jar (skip tests; they run in CI/locally)
RUN ./gradlew bootJar -x test --no-daemon

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

# Render injects PORT; the prod profile binds to it.
ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java -jar app.jar"]
