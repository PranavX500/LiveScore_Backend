# Use lightweight Java 17 image
FROM eclipse-temurin:17-jdk-jammy

# App directory
WORKDIR /app

# Copy all project files
COPY . .

# Build Spring Boot jar
RUN ./mvnw clean package -DskipTests

# Render provides PORT env
ENV PORT=8080

# Expose port
EXPOSE 8080

# Run Spring Boot
CMD ["sh", "-c", "java -jar target/*.jar --server.port=${PORT}"]