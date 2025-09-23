# Environment Variables Setup

## 🔧 Configuration

This project uses environment variables for sensitive configuration. Follow these steps to set up your environment:

### 1. Copy Environment Template
```bash
cp env.example .env
```

### 2. Update .env File
Edit the `.env` file with your actual values:

```bash
# Database Configuration
DB_USERNAME=postgres
DB_PASSWORD=your_secure_password
DB_NAME=yushan
DB_HOST=127.0.0.1
DB_PORT=5432

# Redis Configuration
REDIS_HOST=127.0.0.1
REDIS_PORT=6379

# JWT Configuration
JWT_SECRET=YourSuperSecretKeyHere2024
JWT_ACCESS_TOKEN_EXPIRATION=900000
JWT_REFRESH_TOKEN_EXPIRATION=604800000
JWT_ISSUER=yushan-backend
JWT_ALGORITHM=HS256
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

#### With .env file
```bash
# Load .env file and run
source .env && ./mvnw spring-boot:run
```

#### With environment variables
```bash
# Set variables and run
export JWT_SECRET="YourSecretKey"
export DB_PASSWORD="your_password"
./mvnw spring-boot:run
```

#### With IDE
- **IntelliJ**: Run Configuration → Environment Variables
- **Eclipse**: Run Configuration → Environment tab

### 6. Docker Deployment

```dockerfile
# Dockerfile
ENV JWT_SECRET=YourSecretKey
ENV DB_PASSWORD=your_password
```

```bash
# Docker run
docker run -e JWT_SECRET="YourSecretKey" -e DB_PASSWORD="your_password" your-app
```

### 7. Production Deployment

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

### 8. Troubleshooting

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

### 9. Security Checklist

- [ ] `.env` file is in `.gitignore`
- [ ] `.env` file is never committed to version control
- [ ] JWT_SECRET is different for each environment
- [ ] Database passwords are strong and unique
- [ ] Environment variables are properly loaded
- [ ] Production secrets are managed securely

## 🚀 Quick Start

1. Copy `env.example` to `.env`
2. Update values in `.env`
3. Run `./mvnw spring-boot:run`
4. Application will use environment variables automatically

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
