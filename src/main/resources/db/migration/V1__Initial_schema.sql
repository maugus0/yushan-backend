-- Initial database schema for Yushan Backend
-- This migration creates all the basic tables

CREATE TABLE users if not exists(
    uuid UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(100) NOT NULL,
    hash_password VARCHAR(255) NOT NULL,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    avatar_url VARCHAR(500) NOT NULL,
    profile_detail TEXT,
    birthday DATE,
    gender INTEGER NOT NULL,
    status INTEGER NOT NULL DEFAULT 1,
    is_author BOOLEAN NOT NULL DEFAULT FALSE,
    is_admin BOOLEAN NOT NULL DEFAULT FALSE,
    level INTEGER NOT NULL DEFAULT 1,
    exp DOUBLE NOT NULL PRECISION DEFAULT 0.0,
    yuan DOUBLE NOT NULL PRECISION DEFAULT 0.0,
    read_time DOUBLE NOT NULL PRECISION DEFAULT 0.0,
    read_book_num INTEGER NOT NULL DEFAULT 0,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NOT NULL,
    last_active TIMESTAMP NOT NULL
);

-- Category table (required by novel.category_id FK)
CREATE TABLE category if not exists (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    slug VARCHAR(100) UNIQUE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Novel table
CREATE TABLE novel if not exists(
    id SERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT gen_random_uuid(),
    title VARCHAR(255) NOT NULL,
    author_id UUID NOT NULL,
    author_name VARCHAR(100),
    category_id INTEGER NOT NULL,
    synopsis TEXT,
    cover_img_url VARCHAR(500),
    status INTEGER NOT NULL DEFAULT 0,
    is_completed BOOLEAN DEFAULT FALSE,
    is_valid BOOLEAN DEFAULT TRUE,
    chapter_cnt INTEGER DEFAULT 0,
    word_cnt BIGINT DEFAULT 0,
    avg_rating REAL DEFAULT 0.0,
    review_cnt INTEGER DEFAULT 0,
    view_cnt BIGINT DEFAULT 0,
    vote_cnt INTEGER DEFAULT 0,
    yuan_cnt REAL DEFAULT 0.0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    publish_time TIMESTAMP,
    CONSTRAINT fk_novel_author FOREIGN KEY (author_id) REFERENCES users(uuid),
    CONSTRAINT fk_novel_category FOREIGN KEY (category_id) REFERENCES category(id)
);

CREATE TABLE library if not exists(
    id SERIAL PRIMARY KEY,
    uuid UUID NOT NULL,
    user_id UUID NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE novel_library if not exists (
   id SERIAL PRIMARY KEY,
   library_id INTEGER NOT NULL,
   novel_id INTEGER NOT NULL,
   progress INTEGER NOT NULL,
   create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);