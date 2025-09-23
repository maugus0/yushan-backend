## Local Docker Setup (PostgreSQL + Redis + Spring Boot)

### 1) Prerequisites
- Docker Desktop 4.x+
- Optional: create a `.env` by copying `env.example`

### 2) Bring up the stack
Run all services at once (Postgres, Redis, Backend):
```bash
docker compose up -d
```

Or start step-by-step:
```bash
docker compose up -d postgres redis
```

Start backend (jar runtime):
```bash
docker compose up -d backend
```


### 3) Ports
- API runtime: http://localhost:8080
- Postgres: localhost:5433
- Redis: localhost:6379

### 4) Environment
- Edit `.env` (see `env.example`). Key vars:
  - `DB_USERNAME`, `DB_PASSWORD`, `DB_NAME` (default `yushan`)
  - `JWT_SECRET`
  - `REDIS_HOST`, `REDIS_PORT`

### 5) Spring profile
- Containers use `SPRING_PROFILES_ACTIVE=docker`. See `src/main/resources/application-docker.properties`.
- Base `application.yml` keeps Redis disabled for plain local runs.

### 6) Data persistence
- Postgres data volume: `pg_data`
- Redis data volume: `redis_data`

### 7) Useful commands
```bash
docker compose logs -f backend
docker compose exec postgres psql -U $DB_USERNAME -d $DB_NAME
docker compose down -v   # stop and remove volumes (reset data)
```