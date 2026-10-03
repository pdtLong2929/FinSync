# =======================================================
# Stage 1: Build JAR with Maven & Eclipse Temurin JDK 21
# =======================================================
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Cache Maven dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build package
COPY src ./src
RUN mvn clean package -DskipTests

# =======================================================
# Stage 2: Minimal JRE 21 Runtime Container
# =======================================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Run as unprivileged user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copy built JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
