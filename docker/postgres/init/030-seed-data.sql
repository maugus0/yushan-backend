-- =====================================================
-- 030-seed-data.sql - Initial Seed Data
-- =====================================================

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
