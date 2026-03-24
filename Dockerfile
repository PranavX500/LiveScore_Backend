FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY . .

RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# DO NOT hardcode PORT ❌

# Expose is optional but fine
EXPOSE 8080

CMD ["sh", "-c", "java -jar target/*.jar --server.port=${PORT}"]
