### Prerequisites
- Docker (recommended)
- Java 21 and Maven (for local build)

### Build and Run with maven
mvn exec:java

### Build and Run with Docker
You can build and start the service from the app root:

```bash
docker build -t payment-api .
docker run -p 8080:8080 payment-api
```

The API will be available at `http://localhost:8080`.

## Testing the API

### Automated Tests
Run all unit and integration tests using Maven:

```bash
mvn test
```
