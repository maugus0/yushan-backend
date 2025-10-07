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
    is_admin,
    is_author,
    status,
    level,
    exp,
    yuan,
    read_time,
    read_book_num,
    create_time,
    update_time
) VALUES (
    gen_random_uuid(),
    'admin@yushan.com',
    'admin',
    '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G',
    true,
    true,
    true,
    1,
    1,
    0.0,
    0.0,
    0.0,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Seed data for categories
-- This runs after schema (010) and indexes (020)

-- Insert all categories in one statement
INSERT INTO category (name, description, slug, is_active) VALUES
('Action', 'Action-packed novels with thrilling sequences', 'action', true),
('Adventure', 'Epic journeys and exciting expeditions', 'adventure', true),
('Martial Arts', 'Cultivation and martial arts stories', 'martial-arts', true),
('Fantasy', 'Magical worlds and supernatural elements', 'fantasy', true),
('Sci-Fi', 'Science fiction and futuristic stories', 'sci-fi', true),
('Urban', 'Contemporary city life and modern settings', 'urban', true),
('Historical', 'Stories set in historical periods', 'historical', true),
('Eastern Fantasy', 'Chinese fantasy and mythology', 'eastern-fantasy', true),
('Wuxia', 'Traditional Chinese martial arts fiction', 'wuxia', true),
('Xianxia', 'Immortal heroes and cultivation', 'xianxia', true),
('Military', 'Military strategy and warfare', 'military', true),
('Sports', 'Competitive sports and athletics', 'sports', true),
('Romance', 'Love stories and relationships', 'romance', true),
('Drama', 'Dramatic and emotional narratives', 'drama', true),
('Slice of Life', 'Everyday life and realistic situations', 'slice-of-life', true),
('School Life', 'School and campus stories', 'school-life', true),
('Comedy', 'Humorous and lighthearted tales', 'comedy', true)
ON CONFLICT (slug) DO NOTHING;