-- Initial database schema for Yushan Backend
-- This migration creates all the basic tables

-- Users table
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

-- Category table
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

-- Chapter table
CREATE TABLE chapter (
    id SERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT gen_random_uuid(),
    novel_id INTEGER NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    chapter_num INTEGER NOT NULL,
    word_cnt INTEGER DEFAULT 0,
    is_published BOOLEAN DEFAULT FALSE,
    is_free BOOLEAN DEFAULT TRUE,
    yuan_price REAL DEFAULT 0.0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    publish_time TIMESTAMP,
    CONSTRAINT fk_chapter_novel FOREIGN KEY (novel_id) REFERENCES novel(id)
);

-- Library table (user's personal library)
CREATE TABLE library (
    id SERIAL PRIMARY KEY,
    user_id UUID NOT NULL,
    novel_id INTEGER NOT NULL,
    status INTEGER DEFAULT 0, -- 0: reading, 1: completed, 2: dropped
    add_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_read_time TIMESTAMP,
    CONSTRAINT fk_library_user FOREIGN KEY (user_id) REFERENCES users(uuid),
    CONSTRAINT fk_library_novel FOREIGN KEY (novel_id) REFERENCES novel(id),
    UNIQUE(user_id, novel_id)
);

-- Reading history table
CREATE TABLE history (
    id SERIAL PRIMARY KEY,
    user_id UUID NOT NULL,
    novel_id INTEGER NOT NULL,
    chapter_id INTEGER,
    read_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_history_user FOREIGN KEY (user_id) REFERENCES users(uuid),
    CONSTRAINT fk_history_novel FOREIGN KEY (novel_id) REFERENCES novel(id),
    CONSTRAINT fk_history_chapter FOREIGN KEY (chapter_id) REFERENCES chapter(id)
);

-- Comments table
CREATE TABLE comment (
    id SERIAL PRIMARY KEY,
    user_id UUID NOT NULL,
    novel_id INTEGER NOT NULL,
    content TEXT NOT NULL,
    parent_id INTEGER,
    is_deleted BOOLEAN DEFAULT FALSE,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_comment_user FOREIGN KEY (user_id) REFERENCES users(uuid),
    CONSTRAINT fk_comment_novel FOREIGN KEY (novel_id) REFERENCES novel(id),
    CONSTRAINT fk_comment_parent FOREIGN KEY (parent_id) REFERENCES comment(id)
);

-- Reviews table
CREATE TABLE review (
    id SERIAL PRIMARY KEY,
    user_id UUID NOT NULL,
    novel_id INTEGER NOT NULL,
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    content TEXT,
    is_deleted BOOLEAN DEFAULT FALSE,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_review_user FOREIGN KEY (user_id) REFERENCES users(uuid),
    CONSTRAINT fk_review_novel FOREIGN KEY (novel_id) REFERENCES novel(id),
    UNIQUE(user_id, novel_id)
);

-- Novel library junction table (for many-to-many relationship)
CREATE TABLE novel_library (
    id SERIAL PRIMARY KEY,
    user_id UUID NOT NULL,
    novel_id INTEGER NOT NULL,
    add_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_novel_library_user FOREIGN KEY (user_id) REFERENCES users(uuid),
    CONSTRAINT fk_novel_library_novel FOREIGN KEY (novel_id) REFERENCES novel(id),
    UNIQUE(user_id, novel_id)
);
