-- Seed data for initial setup
-- This migration adds basic data needed for the application

-- Insert default admin user
-- Email: admin@yushan.com
-- Password: admin (hashed with BCrypt)
INSERT INTO users (
    uuid,
    email,
    username,
    hash_password,
    email_verified,
    avatar_url,
    gender,
    is_admin,
    is_author,
    status,
    level,
    exp,
    yuan,
    read_time,
    read_book_num,
    create_time,
    update_time,
    last_login,
    last_active
) VALUES (
    gen_random_uuid(),
    'admin@yushan.com',
    'admin',
    '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G',
    true,
    'user.png',
    0,
    true,
    true,
    1,
    1,
    0.0,
    0.0,
    0.0,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Seed data for categories
-- This runs after schema (010) and indexes (020)

-- Insert all categories in one statement
INSERT INTO category (name, description, slug, is_active, create_time, update_time) VALUES
('Action', 'Action-packed novels with thrilling sequences', 'action', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Adventure', 'Epic journeys and exciting expeditions', 'adventure', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Martial Arts', 'Cultivation and martial arts stories', 'martial-arts', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Fantasy', 'Magical worlds and supernatural elements', 'fantasy', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Sci-Fi', 'Science fiction and futuristic stories', 'sci-fi', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Urban', 'Contemporary city life and modern settings', 'urban', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Historical', 'Stories set in historical periods', 'historical', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Eastern Fantasy', 'Chinese fantasy and mythology', 'eastern-fantasy', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Wuxia', 'Traditional Chinese martial arts fiction', 'wuxia', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Xianxia', 'Immortal heroes and cultivation', 'xianxia', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Military', 'Military strategy and warfare', 'military', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Sports', 'Competitive sports and athletics', 'sports', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Romance', 'Love stories and relationships', 'romance', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Drama', 'Dramatic and emotional narratives', 'drama', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Slice of Life', 'Everyday life and realistic situations', 'slice-of-life', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('School Life', 'School and campus stories', 'school-life', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Comedy', 'Humorous and lighthearted tales', 'comedy', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);