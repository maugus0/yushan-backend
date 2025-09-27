# Environment Variables Setup

## 🔧 Configuration

This project uses environment variables for sensitive configuration across multiple environments and tools. Follow these steps to set up your environment:

### 1. Copy Environment Template
```bash
cp env.example .env
```

### 2. Update .env File
Edit the `.env` file with your actual values:

```bash
# Database Configuration (for Spring Boot application)
DB_USERNAME=postgres
DB_PASSWORD=your_secure_password
DB_NAME=yushan
DB_HOST=127.0.0.1
DB_PORT=5432

# MyBatis Generator Configuration (for code generation)
GENERATOR_DB_USERNAME=postgres
GENERATOR_DB_PASSWORD=your_secure_password
GENERATOR_DB_NAME=yushan
GENERATOR_DB_HOST=127.0.0.1
GENERATOR_DB_PORT=5432
# Optional: Override driver location if needed
# GENERATOR_DRIVER_LOCATION=/path/to/your/postgresql-driver.jar

# Redis Configuration
REDIS_HOST=127.0.0.1
REDIS_PORT=6379
REDIS_PASSWORD=

# JWT Configuration
JWT_SECRET=YourSuperSecretKeyHere2024
JWT_ACCESS_TOKEN_EXPIRATION=900000
JWT_REFRESH_TOKEN_EXPIRATION=604800000
JWT_ISSUER=yushan-backend
JWT_ALGORITHM=HS256

# Server Configuration
SERVER_PORT=8010
```

### 3. Security Best Practices

#### JWT Secret Key
- **Minimum 32 characters**
- **Mix of letters, numbers, and symbols**
- **Different for each environment** (dev, staging, production)

#### Database Password
- **Strong password** (minimum 12 characters)
- **Different for each environment**
- **Never commit to version control**

### 4. Environment-Specific Configuration

#### Development
```bash
JWT_SECRET=DevSecretKey2024
DB_PASSWORD=dev_password
DB_NAME=yushan
DB_HOST=127.0.0.1
DB_PORT=5432
REDIS_HOST=127.0.0.1
REDIS_PORT=6379
```

#### Staging
```bash
JWT_SECRET=StagingSecretKey2024
DB_PASSWORD=staging_password
DB_NAME=yushan
DB_HOST=<staging-db-host>
DB_PORT=5432
REDIS_HOST=<staging-redis-host>
REDIS_PORT=6379
```

#### Production
```bash
JWT_SECRET=ProductionSecretKey2024
DB_PASSWORD=production_password
DB_NAME=yushan
DB_HOST=<prod-db-host>
DB_PORT=5432
REDIS_HOST=<prod-redis-host>
REDIS_PORT=6379
```

### 5. Running the Application

#### 5.1 Spring Boot Application

##### With .env file
```bash
# Load .env file and run
source .env && ./mvnw spring-boot:run
```

##### With environment variables
```bash
# Set variables and run
export JWT_SECRET="YourSecretKey"
export DB_PASSWORD="your_password"
./mvnw spring-boot:run
```

##### With Spring Profiles
```bash
# Local development (default profile)
./mvnw spring-boot:run

# Docker profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=docker

# Test profile (for unit tests)
./mvnw spring-boot:run -Dspring-boot.run.profiles=test

# Integration test profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=integration-test

# Staging profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=staging
```

##### With multiple profiles
```bash
# Combine profiles (e.g., docker + staging)
./mvnw spring-boot:run -Dspring-boot.run.profiles=docker,staging
```

##### With IDE
- **IntelliJ**: Run Configuration → Environment Variables → Active Profiles
- **Eclipse**: Run Configuration → Environment tab → Program Arguments: `--spring.profiles.active=docker`

#### 5.2 MyBatis Generator (Code Generation)

##### With .env file
```bash
# Load .env file and run generator
source .env && ./mvnw mybatis-generator:generate
```

##### With environment variables
```bash
# Set generator variables and run
export GENERATOR_DB_PASSWORD="your_password"
export GENERATOR_DB_NAME="yushan"
./mvnw mybatis-generator:generate
```

##### With custom driver location
```bash
# Override driver location
export GENERATOR_DRIVER_LOCATION="/path/to/your/postgresql-driver.jar"
./mvnw mybatis-generator:generate
```

#### 5.3 Testing

##### Unit Tests (H2 Database)
```bash
# Run unit tests with H2 in-memory database (test profile)
./mvnw test

# Run with specific test profile
./mvnw test -Dspring.profiles.active=test
```

##### Integration Tests (PostgreSQL + Redis)
```bash
# Run integration tests with Testcontainers (integration-test profile)
./mvnw verify

# Run with specific integration test profile
./mvnw test -Dspring.profiles.active=integration-test
```

##### Test with specific profiles
```bash
# Test with docker profile
./mvnw test -Dspring.profiles.active=docker

# Test with staging profile
./mvnw test -Dspring.profiles.active=staging
```

#### 5.4 Docker Environment

##### With Docker Compose
```bash
# Start all services (PostgreSQL + Redis + Backend)
docker-compose up -d

# View logs
docker-compose logs -f backend
```

##### With Docker only
```bash
# Build image
docker build -t yushan-backend .

# Run with port mapping only (uses default config)
docker run -p 8080:8080 yushan-backend

# Run with environment variables only (no port access)
docker run -e JWT_SECRET="YourSecretKey" -e DB_PASSWORD="your_password" yushan-backend

# Run with both port mapping AND environment variables (recommended)
docker run -p 8080:8080 -e JWT_SECRET="YourSecretKey" -e DB_PASSWORD="your_password" yushan-backend

# Run with specific profile
docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=docker -e JWT_SECRET="YourSecretKey" yushan-backend

# Run with staging profile
docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=staging -e STAGING_DATABASE_URL="your_url" yushan-backend

# Run with multiple environment variables
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=docker \
  -e JWT_SECRET="YourSecretKey" \
  -e DB_PASSWORD="your_password" \
  -e DB_HOST="postgres" \
  -e REDIS_HOST="redis" \
  yushan-backend
```

### 6. Configuration Files Overview

#### 6.1 Application Configuration Files

| **File** | **Profile** | **Purpose** | **Environment** | **Database** | **Redis** |
|---|---|---|---|---|---|
| `application.yml` | `default` | Default config | Local development | PostgreSQL | Disabled |
| `application-docker.properties` | `docker` | Docker environment | Docker containers | PostgreSQL | Enabled |
| `application-test.properties` | `test` | Unit tests | Testing | H2 in-memory | Disabled |
| `application-integration-test.properties` | `integration-test` | Integration tests | Testing | PostgreSQL (Testcontainers) | Redis (Testcontainers) |
| `application-staging.properties` | `staging` | Staging environment | Railway deployment | Supabase PostgreSQL | Upstash Redis |

#### 6.2 Profile Activation Methods

##### Command Line
```bash
# Maven
./mvnw spring-boot:run -Dspring-boot.run.profiles=docker

# Java JAR
java -jar app.jar --spring.profiles.active=docker

# Multiple profiles
java -jar app.jar --spring.profiles.active=docker,staging
```

##### Environment Variables
```bash
# Set profile via environment variable
export SPRING_PROFILES_ACTIVE=docker
./mvnw spring-boot:run

# In .env file
echo "SPRING_PROFILES_ACTIVE=docker" >> .env
```

##### IDE Configuration
- **IntelliJ**: Run Configuration → Active Profiles → `docker`
- **Eclipse**: Run Configuration → Program Arguments → `--spring.profiles.active=docker`
- **VS Code**: launch.json → `"args": ["--spring.profiles.active=docker"]`

##### Docker
```bash
# Docker run
docker run -e SPRING_PROFILES_ACTIVE=docker your-app

# Docker Compose
# In docker-compose.yml
environment:
  - SPRING_PROFILES_ACTIVE=docker
```

#### 6.3 MyBatis Generator Files

| **File** | **Purpose** | **Usage** |
|---|---|---|
| `generatorConfig.xml` | Generator configuration | Code generation rules |
| `generator.properties` | Database connection | Environment variables for generator |

#### 6.4 Environment Variables Usage

```bash
# Spring Boot Application
DB_USERNAME, DB_PASSWORD, DB_NAME, DB_HOST, DB_PORT
REDIS_HOST, REDIS_PORT, REDIS_PASSWORD
JWT_SECRET, JWT_ACCESS_TOKEN_EXPIRATION, JWT_REFRESH_TOKEN_EXPIRATION
SERVER_PORT

# MyBatis Generator
GENERATOR_DB_USERNAME, GENERATOR_DB_PASSWORD, GENERATOR_DB_NAME
GENERATOR_DB_HOST, GENERATOR_DB_PORT
GENERATOR_DRIVER_LOCATION (optional)
```

### 7. Docker Deployment

#### 7.1 Dockerfile Configuration
```dockerfile
# Dockerfile
ENV JWT_SECRET=YourSecretKey
ENV DB_PASSWORD=your_password
```

#### 7.2 Docker Run Commands

##### Basic Run (Port Only)
```bash
# Run with port mapping only
docker run -p 8080:8080 yushan-backend
# Access: http://localhost:8080
```

##### Environment Variables Only
```bash
# Run with environment variables only (no port access)
docker run -e JWT_SECRET="YourSecretKey" -e DB_PASSWORD="your_password" yushan-backend
# Note: Container runs but not accessible from host
```

##### Complete Setup (Recommended)
```bash
# Run with both port mapping AND environment variables
docker run -p 8080:8080 -e JWT_SECRET="YourSecretKey" -e DB_PASSWORD="your_password" yushan-backend
# Access: http://localhost:8080
```

##### With Profile
```bash
# Run with specific profile
docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=docker yushan-backend
```

#### 7.3 Port Mapping Explanation

| **Command** | **Port Mapping** | **Access** | **Use Case** |
|---|---|---|---|
| `docker run yushan-backend` | ❌ No mapping | ❌ Not accessible | Background service |
| `docker run -p 8080:8080 yushan-backend` | ✅ 8080:8080 | ✅ localhost:8080 | Development |
| `docker run -p 3000:8080 yushan-backend` | ✅ 3000:8080 | ✅ localhost:3000 | Custom port |
| `docker run -e VAR=value yushan-backend` | ❌ No mapping | ❌ Not accessible | Environment only |

### 8. Production Deployment

#### System Environment Variables
```bash
# /etc/environment
JWT_SECRET=YourProductionSecretKey
DB_PASSWORD=your_production_password
```

#### Kubernetes ConfigMap
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: app-config
data:
  JWT_SECRET: "YourSecretKey"
  DB_PASSWORD: "your_password"
```

### 9. Troubleshooting

#### Common Issues

1. **Environment variables not loaded**
   - Check if .env file exists
   - Verify variable names match exactly
   - Restart application after changes

2. **JWT token validation fails**
   - Ensure JWT_SECRET is the same across restarts
   - Check JWT_SECRET length (minimum 32 characters)

3. **Database connection fails**
   - Verify DB_PASSWORD is correct
   - Check database server is running
   - Verify connection string

4. **MyBatis Generator fails**
   - Check if PostgreSQL is running
   - Verify GENERATOR_DB_* variables are set
   - Check if database exists and has tables
   - Verify driver location (use Maven's local repository)

5. **Docker connection issues**
   - Check if containers are running: `docker-compose ps`
   - Verify port mappings: `docker-compose port postgres 5432`
   - Check logs: `docker-compose logs postgres`

6. **Test failures**
   - Unit tests: Check H2 database configuration
   - Integration tests: Ensure Docker is running for Testcontainers
   - Check if test database is clean

### 10. Security Checklist

- [ ] `.env` file is in `.gitignore`
- [ ] `.env` file is never committed to version control
- [ ] JWT_SECRET is different for each environment
- [ ] Database passwords are strong and unique
- [ ] Environment variables are properly loaded
- [ ] Production secrets are managed securely
- [ ] Generator database credentials are separate from application credentials

## 🚀 Quick Start

### Development Setup
1. Copy `env.example` to `.env`
2. Update values in `.env`
3. Start PostgreSQL and Redis (or use Docker)
4. Run `./mvnw spring-boot:run` (default profile)

### Code Generation
1. Ensure PostgreSQL is running
2. Create database tables in pgAdmin4
3. Run `./mvnw mybatis-generator:generate`
4. Generated code will appear in `src/main/java/com/yushan/backend/`

### Testing
1. **Unit Tests**: `./mvnw test` (uses H2, test profile)
2. **Integration Tests**: `./mvnw verify` (uses Testcontainers, integration-test profile)

### Docker Development
1. Run `docker-compose up -d`
2. Application available at `http://localhost:8010`
3. PostgreSQL available at `localhost:5433`
4. Redis available at `localhost:6379`

### Docker Run (Single Container)
1. Build image: `docker build -t yushan-backend .`
2. Run with port: `docker run -p 8080:8080 yushan-backend`
3. Access: `http://localhost:8080`

### Profile-Specific Development
1. **Local Development**: `./mvnw spring-boot:run` (default profile)
2. **Docker Development**: `./mvnw spring-boot:run -Dspring-boot.run.profiles=docker`
3. **Staging Testing**: `./mvnw spring-boot:run -Dspring-boot.run.profiles=staging`

## 📋 Environment Summary

### Local Development (No Docker)
- **Database**: PostgreSQL on `localhost:5432`
- **Redis**: Disabled (auto-configuration excluded)
- **Profile**: `default` (`application.yml`)
- **Command**: `./mvnw spring-boot:run`

### Docker Development
- **Database**: PostgreSQL container on `localhost:5433`
- **Redis**: Redis container on `localhost:6379`
- **Profile**: `docker` (`application-docker.properties`)
- **Command**: `./mvnw spring-boot:run -Dspring-boot.run.profiles=docker`

### Unit Testing
- **Database**: H2 in-memory
- **Redis**: Disabled
- **Profile**: `test` (`application-test.properties`)
- **Command**: `./mvnw test`

### Integration Testing
- **Database**: PostgreSQL (Testcontainers)
- **Redis**: Redis (Testcontainers)
- **Profile**: `integration-test` (`application-integration-test.properties`)
- **Command**: `./mvnw verify`

### Staging
- **Database**: Supabase PostgreSQL
- **Redis**: Upstash Redis
- **Profile**: `staging` (`application-staging.properties`)
- **Command**: `./mvnw spring-boot:run -Dspring-boot.run.profiles=staging`

### ℹ️ Notes for Local vs Docker
- Docker stack maps Postgres host port to 5433. From host tools (pgAdmin/psql), connect: Host `127.0.0.1`, Port `5433`.
- From another container in the compose network, connect to `postgres:5432` (service name, container port).
- If you run locally without Docker, ensure your local Postgres listens on 127.0.0.1:5432 or adjust `DB_HOST/DB_PORT` accordingly.

### Example: Override DB for local run (no Docker)
```bash
export DB_HOST=127.0.0.1
export DB_PORT=5432
export DB_NAME=yushan
./mvnw spring-boot:run
```
