-- Seed data for initial setup
-- This migration adds basic data needed for the application

-- Insert default categories
INSERT INTO category (name, description, slug, is_active) VALUES
('Fantasy', 'Fantasy novels with magical elements', 'fantasy', true),
('Romance', 'Romance and love stories', 'romance', true),
('Mystery', 'Mystery and detective stories', 'mystery', true),
('Sci-Fi', 'Science fiction novels', 'sci-fi', true),
('Horror', 'Horror and thriller stories', 'horror', true),
('Adventure', 'Adventure and action stories', 'adventure', true),
('Drama', 'Dramatic and emotional stories', 'drama', true),
('Comedy', 'Comedy and humorous stories', 'comedy', true)
ON CONFLICT (id) DO NOTHING;

-- Insert default admin user (password: admin123)
INSERT INTO users (uuid, email, username, hash_password, email_verified, is_author, author_verified, status) VALUES
(gen_random_uuid(), 'admin@yushan.com', 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', true, true, true, 1)
ON CONFLICT (email) DO NOTHING;
