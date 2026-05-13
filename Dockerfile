FROM maven:3.9.11-eclipse-temurin-21

WORKDIR /app

# Copy pom.xml and download dependencies (layer caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and compile
COPY src ./src
RUN mvn compile -B

# Expose the application port
EXPOSE 8080

# Run the application using exec:java
CMD ["mvn", "exec:java", "-Dexec.mainClass=com.example.DemoApplication"]
