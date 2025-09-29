-- Basic schema for local development (PostgreSQL)
-- NOTE: These scripts run ONLY when the Postgres data directory is empty

CREATE TABLE IF NOT EXISTS users (
    uuid UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(100) NOT NULL,
    hash_password VARCHAR(255) NOT NULL,
    email_verified BOOLEAN DEFAULT FALSE,
    avatar_url VARCHAR(500),
    profile_detail TEXT,
    birthday DATE,
    gender INTEGER,
    status INTEGER DEFAULT 1,
    is_author BOOLEAN DEFAULT FALSE,
    author_verified BOOLEAN DEFAULT FALSE,
    level INTEGER DEFAULT 1,
    exp DOUBLE PRECISION DEFAULT 0.0,
    yuan DOUBLE PRECISION DEFAULT 0.0,
    read_time DOUBLE PRECISION DEFAULT 0.0,
    read_book_num INTEGER DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP,
    last_active TIMESTAMP
);