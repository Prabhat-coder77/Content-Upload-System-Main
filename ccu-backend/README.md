# Course Content Upload System

A production-grade Java Spring Boot backend for managing course content uploads.

## Tech Stack
- Java 17
- Spring Boot 3.2.x
- Spring Security (JWT)
- Spring Data JPA
- PostgreSQL
- Flyway
- AWS S3
- Docker & Docker Compose
- Swagger/OpenAPI

## Setup & Run

### How to run via docker-compose
1. Ensure Docker and Docker Compose are installed.
2. Build and start the containers:
   ```bash
   docker-compose up -d --build
   ```
3. The application will be available at `http://localhost:8080`.
4. The database is exposed on `localhost:5432`.

### How to run locally (Without Docker)

The application utilizes `spring-dotenv` to seamlessly inject `.env` configurations.

1. Ensure you have Java 17 installed.
2. Ensure you have a local PostgreSQL database running on `localhost:5432` with a database named `ccu_db`, or use the one from docker-compose: `docker-compose up -d db`.
3. Create a `.env` file in the root directory. You can use `.env.example` as a template:
   ```bash
   cp .env.example .env
   ```
4. Update the values in `.env` as required (example below):
   ```env
   DB_HOST=localhost
   DB_PORT=5432
   DB_NAME=ccu_db
   DB_USER=postgres
   DB_PASSWORD=postgres
   JWT_SECRET=8f4e6b19a164b73bd540f2f36fcd8f2441d8e3cc7bc2e2f3d97e201b2298c5ee
   STORAGE_LOCAL_ROOT=uploads
   ```

5. Run the application directly using the Maven wrapper:
   ```bash
   ./mvnw spring-boot:run
   ```

### Storage Configuration (Local vs S3)
The application supports storing files locally or in AWS S3.
- To use local storage, run the application with the `dev` profile (default). Ensure `STORAGE_LOCAL_ROOT` is set correctly.
- To use S3 storage, run the application with the `prod` profile (`SPRING_PROFILES_ACTIVE=prod`). 
- When using S3, provide AWS credentials via standard environment variables (`AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`) and configure `S3_BUCKET`, `S3_PREFIX`, and `S3_REGION` in environment variables.

### API Documentation
Swagger UI is available at: `http://localhost:8080/swagger-ui/index.html`
OpenAPI specification: `http://localhost:8080/v3/api-docs`

## Example Requirements

### 1. Register a new user
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "password123", "role": "USER"}'
```

### 2. Login & Get JWT
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "password123"}'
```
Extract the `accessToken` from the response.

### 3. Upload a file
Replace `<YOUR_JWT_TOKEN>` with the token from step 2.
```bash
curl -X POST http://localhost:8080/api/v1/contents \
  -H "Authorization: Bearer <YOUR_JWT_TOKEN>" \
  -F "file=@/path/to/your/testfile.pdf"
```

### 4. List files
```bash
curl -X GET "http://localhost:8080/api/v1/contents?page=0&size=10" \
  -H "Authorization: Bearer <YOUR_JWT_TOKEN>"
```

### 5. Download a file
Replace `<FILE_ID>` with the UUID from step 4.
```bash
curl -X GET http://localhost:8080/api/v1/contents/<FILE_ID>/download \
  -H "Authorization: Bearer <YOUR_JWT_TOKEN>" \
  --output downloaded_file.pdf
```

## Testing
Run the test suite using Maven. The project uses Testcontainers to spin up a PostgreSQL instance for integration testing. Ensure Docker is running.
```bash
./mvnw clean test
```
