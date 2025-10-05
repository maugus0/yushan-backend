-- Initial database schema for Yushan Backend
-- This migration creates all the basic tables

CREATE TABLE users (
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
    is_admin BOOLEAN DEFAULT FALSE,
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

-- Category table (required by novel.category_id FK)
CREATE TABLE category (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    slug VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Novel table
CREATE TABLE novel (
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

CREATE TABLE library (
    id SERIAL PRIMARY KEY,
    uuid UUID NOT NULL,
    user_id UUID NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE novel_library (
   id SERIAL PRIMARY KEY,
   library_id INTEGER NOT NULL,
   novel_id INTEGER NOT NULL,
   progress INTEGER NOT NULL,
   create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);