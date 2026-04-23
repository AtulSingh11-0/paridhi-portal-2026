# ==========================================
# BUILD STAGE
# ==========================================
FROM eclipse-temurin:21.0.10_7-jdk-ubi9-minimal AS build
WORKDIR /app

# 1. Cache the Maven wrapper and dependencies FIRST
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Make wrapper executable and download dependencies (Caches heavily)
RUN chmod +x mvnw && \
    ./mvnw dependency:go-offline -B

# 2. Copy source code and build AFTER dependencies are cached
COPY src src
RUN ./mvnw package -DskipTests

# ==========================================
# RUNTIME STAGE
# ==========================================
FROM eclipse-temurin:21.0.10_7-jre-ubi9-minimal
WORKDIR /app

# SECURITY FIX: Force the OS to download the absolute latest security patches for all installed packages
USER root
RUN microdnf update -y && microdnf clean all

# Create a non-root user to run the application securely
# Create necessary writable directories for Java temp files
RUN groupadd -r spring && useradd -r -g spring spring && \
    mkdir -p /tmp/spring && \
    mkdir -p /var/log/spring && \
    chown -R spring:spring /tmp/spring && \
    chown -R spring:spring /var/log/spring

# Switch to the non-root user
USER spring:spring

# Copy only the built JAR from the build stage
COPY --from=build --chown=spring:spring /app/target/*.jar app.jar

# JVM Optimizations (Container-aware, 75% RAM limit)
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=50.0 -Xss256k -XX:+ExitOnOutOfMemoryError -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp/spring -XX:+UseG1GC -XX:MaxGCPauseMillis=100 -XX:+UseStringDeduplication -Djava.security.egd=file:/dev/./urandom -Djava.io.tmpdir=/tmp/spring"

# Expose the application port
EXPOSE 8080

# Run the application with optimization flags and UTC+5:30 Timezone
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Duser.timezone=Asia/Kolkata -jar app.jar"]