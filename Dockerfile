FROM maven:3.9.9-eclipse-temurin-21 AS builder
USER root
WORKDIR /app
COPY . .
RUN mvn package -DskipTests

# Stage 2: Minimal runtime image
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copying the extracted quarkus-app from builder stage
COPY --from=builder /app/target/quarkus-app/lib/ /app/lib/
COPY --from=builder /app/target/quarkus-app/*.jar /app/
COPY --from=builder /app/target/quarkus-app/app/ /app/app/
COPY --from=builder /app/target/quarkus-app/quarkus/ /app/quarkus/

EXPOSE 8080

CMD ["java", "-jar", "/app/quarkus-run.jar"]
