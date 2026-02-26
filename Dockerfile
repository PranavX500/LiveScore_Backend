FROM eclipse-temurin:17-jdk-jammy

# App directory
WORKDIR /app

# Copy project
COPY . .

# Give execute permission to Maven wrapper
RUN chmod +x mvnw

# Build Spring Boot jar
RUN ./mvnw clean package -DskipTests

# Platform provides PORT (Railway/Render)
ENV PORT=8080

# Expose container port
EXPOSE 8080

# Run Spring Boot with platform PORT
CMD ["sh", "-c", "java -jar target/*.jar --server.port=${PORT}"]
