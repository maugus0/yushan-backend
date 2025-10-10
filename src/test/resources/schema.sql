-- Test database schema for H2
-- This file is automatically executed by Spring Boot Test

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    uuid VARCHAR(36) PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(100) NOT NULL,
    hash_password VARCHAR(255) NOT NULL,
    email_verified BOOLEAN DEFAULT FALSE,
    avatar_url TEXT,
    profile_detail TEXT,
    birthday DATE,
    gender INTEGER,
    status INTEGER DEFAULT 1,
    is_author BOOLEAN DEFAULT FALSE,
    is_admin BOOLEAN DEFAULT FALSE,
    level INTEGER DEFAULT 1,
    exp DOUBLE DEFAULT 0.0,
    yuan DOUBLE DEFAULT 0.0,
    read_time DOUBLE DEFAULT 0.0,
    read_book_num INTEGER DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP,
    last_active TIMESTAMP
);

CREATE TABLE IF NOT EXISTS category (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    slug VARCHAR(100) UNIQUE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS novel (
    id SERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT RANDOM_UUID(),
    title VARCHAR(255) NOT NULL,
    author_id UUID NOT NULL,
    author_name VARCHAR(100),
    category_id INTEGER NOT NULL,
    synopsis TEXT,
    cover_img_url TEXT,
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

CREATE TABLE IF NOT EXISTS chapter (
    id SERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT RANDOM_UUID(),
    novel_id INTEGER NOT NULL,
    chapter_number INTEGER NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    word_cnt INTEGER DEFAULT 0,
    is_premium BOOLEAN DEFAULT FALSE,
    yuan_cost REAL DEFAULT 0.0,
    view_cnt BIGINT DEFAULT 0,
    is_valid BOOLEAN DEFAULT TRUE,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    publish_time TIMESTAMP,
    CONSTRAINT fk_chapter_novel FOREIGN KEY (novel_id) REFERENCES novel(id) ON DELETE CASCADE,
    CONSTRAINT unique_novel_chapter_number UNIQUE (novel_id, chapter_number)
);

-- Comment table
CREATE TABLE IF NOT EXISTS comment (
    id SERIAL PRIMARY KEY,
    user_id UUID NOT NULL,
    chapter_id INTEGER NOT NULL,
    content TEXT NOT NULL,
    like_cnt INTEGER DEFAULT 0,
    is_spoiler BOOLEAN DEFAULT FALSE,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_comment_user FOREIGN KEY (user_id) REFERENCES users(uuid) ON DELETE CASCADE,
    CONSTRAINT fk_comment_chapter FOREIGN KEY (chapter_id) REFERENCES chapter(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS library (
    id SERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT RANDOM_UUID(),
    user_id UUID NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS novel_library (
    id SERIAL PRIMARY KEY,
    library_id INTEGER NOT NULL,
    novel_id INTEGER NOT NULL,
    progress INTEGER NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE review (
    id SERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT RANDOM_UUID(),
    user_id UUID NOT NULL,
    novel_id INTEGER NOT NULL,
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    title VARCHAR(255),
    content TEXT,
    like_cnt INTEGER DEFAULT 0,
    is_spoiler BOOLEAN DEFAULT FALSE,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_review_user FOREIGN KEY (user_id) REFERENCES users(uuid) ON DELETE CASCADE,
    CONSTRAINT fk_review_novel FOREIGN KEY (novel_id) REFERENCES novel(id) ON DELETE CASCADE,
    CONSTRAINT unique_user_novel_review UNIQUE (user_id, novel_id)
);

CREATE TABLE vote (
    id SERIAL PRIMARY KEY,
    user_id UUID NOT NULL,
    novel_id INTEGER NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE, -- Soft delete flag
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_vote_user FOREIGN KEY (user_id) REFERENCES users(uuid) ON DELETE CASCADE,
    CONSTRAINT fk_vote_novel FOREIGN KEY (novel_id) REFERENCES novel(id) ON DELETE CASCADE,
    CONSTRAINT unique_user_novel_vote UNIQUE (user_id, novel_id)
);