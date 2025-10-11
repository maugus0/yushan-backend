-- Comprehensive seed data for Yushan Backend
-- This migration adds extensive test data for all tables
-- Each table will have approximately 20 records with proper FK relationships

-- Insert additional users (author and regular user)
-- Password: admin (hashed with BCrypt) - same as admin user
INSERT INTO users (
    uuid, email, username, hash_password, email_verified, avatar_url,
    profile_detail, birthday, gender, is_admin, is_author,
    status,level,exp,yuan,read_time,read_book_num,create_time,update_time,last_login,last_active
) VALUES 
-- Author user
(
    gen_random_uuid(), 'author@yushan.com', 'author', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G',
    true, 'author.png', 'Professional author with years of experience in fantasy and sci-fi writing.',
    '1985-03-15', 1, false, true, 1, 5, 1500.0, 250.0, 120.5, 45,
    CURRENT_TIMESTAMP - INTERVAL '30 days',
    CURRENT_TIMESTAMP - INTERVAL '1 day',
    CURRENT_TIMESTAMP - INTERVAL '2 hours',
    CURRENT_TIMESTAMP - INTERVAL '30 minutes'
),
-- Regular user
(
    gen_random_uuid(),'user@yushan.com', 'user','$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G',
    true, 'user.png', 'Avid reader who loves fantasy and adventure novels.',
    '1992-07-22', 0, false, false, 1, 3, 450.0, 75.0, 85.2, 12,
    CURRENT_TIMESTAMP - INTERVAL '15 days',
    CURRENT_TIMESTAMP - INTERVAL '3 hours',
    CURRENT_TIMESTAMP - INTERVAL '1 hour',
    CURRENT_TIMESTAMP - INTERVAL '15 minutes'
);

-- Insert 18 additional users for comprehensive testing
INSERT INTO users (
    uuid,
    email,
    username,
    hash_password,
    email_verified,
    avatar_url,
    profile_detail,
    birthday,
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
) VALUES 
(gen_random_uuid(), 'reader1@yushan.com', 'reader1', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user.png', 'Fantasy enthusiast', '1990-01-15', 0, false, false, 1, 2, 200.0, 50.0, 45.0, 8, CURRENT_TIMESTAMP - INTERVAL '20 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '3 hours', CURRENT_TIMESTAMP - INTERVAL '1 hour'),
(gen_random_uuid(), 'reader2@yushan.com', 'reader2', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user.png', 'Sci-fi lover', '1988-05-20', 1, false, false, 1, 4, 800.0, 120.0, 95.0, 18, CURRENT_TIMESTAMP - INTERVAL '25 days', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '5 hours', CURRENT_TIMESTAMP - INTERVAL '2 hours'),
(gen_random_uuid(), 'reader3@yushan.com', 'reader3', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user.png', 'Romance reader', '1995-12-10', 0, false, false, 1, 1, 100.0, 25.0, 20.0, 3, CURRENT_TIMESTAMP - INTERVAL '10 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 hour', CURRENT_TIMESTAMP - INTERVAL '30 minutes'),
(gen_random_uuid(), 'reader4@yushan.com', 'reader4', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user.png', 'Action fan', '1987-08-03', 1, false, false, 1, 3, 600.0, 90.0, 70.0, 15, CURRENT_TIMESTAMP - INTERVAL '18 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '4 hours', CURRENT_TIMESTAMP - INTERVAL '1 hour'),
(gen_random_uuid(), 'reader5@yushan.com', 'reader5', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user.png', 'Martial arts enthusiast', '1993-11-25', 0, false, false, 1, 2, 300.0, 60.0, 40.0, 7, CURRENT_TIMESTAMP - INTERVAL '12 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '2 hours', CURRENT_TIMESTAMP - INTERVAL '45 minutes'),
(gen_random_uuid(), 'author2@yushan.com', 'author2', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'author2.png', 'Professional fantasy author', '1980-04-12', 1, false, true, 1, 6, 2000.0, 300.0, 150.0, 60, CURRENT_TIMESTAMP - INTERVAL '45 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 hour', CURRENT_TIMESTAMP - INTERVAL '20 minutes'),
(gen_random_uuid(), 'author3@yushan.com', 'author3', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'author3.png', 'Sci-fi writer', '1982-09-18', 0, false, true, 1, 5, 1800.0, 280.0, 140.0, 55, CURRENT_TIMESTAMP - INTERVAL '40 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '2 hours', CURRENT_TIMESTAMP - INTERVAL '30 minutes'),
(gen_random_uuid(), 'reader6@yushan.com', 'reader6', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user.png', 'Urban fantasy reader', '1991-06-14', 1, false, false, 1, 3, 500.0, 80.0, 60.0, 10, CURRENT_TIMESTAMP - INTERVAL '22 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '3 hours', CURRENT_TIMESTAMP - INTERVAL '1 hour'),
(gen_random_uuid(), 'reader7@yushan.com', 'reader7', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user.png', 'Historical fiction lover', '1989-02-28', 0, false, false, 1, 2, 250.0, 40.0, 35.0, 6, CURRENT_TIMESTAMP - INTERVAL '14 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 hour', CURRENT_TIMESTAMP - INTERVAL '30 minutes'),
(gen_random_uuid(), 'reader8@yushan.com', 'reader8', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user.png', 'Comedy enthusiast', '1994-10-07', 1, false, false, 1, 1, 150.0, 30.0, 25.0, 4, CURRENT_TIMESTAMP - INTERVAL '8 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '30 minutes', CURRENT_TIMESTAMP - INTERVAL '15 minutes'),
(gen_random_uuid(), 'reader9@yushan.com', 'reader9', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user.png', 'Drama reader', '1986-03-30', 0, false, false, 1, 4, 700.0, 110.0, 85.0, 16, CURRENT_TIMESTAMP - INTERVAL '28 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '4 hours', CURRENT_TIMESTAMP - INTERVAL '2 hours'),
(gen_random_uuid(), 'reader10@yushan.com', 'reader10', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user.png', 'Adventure seeker', '1992-12-05', 1, false, false, 1, 3, 400.0, 70.0, 50.0, 9, CURRENT_TIMESTAMP - INTERVAL '16 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '2 hours', CURRENT_TIMESTAMP - INTERVAL '1 hour'),
(gen_random_uuid(), 'author4@yushan.com', 'author4', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'author4.png', 'Romance novelist', '1978-07-22', 0, false, true, 1, 7, 2500.0, 400.0, 200.0, 80, CURRENT_TIMESTAMP - INTERVAL '50 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 hour', CURRENT_TIMESTAMP - INTERVAL '25 minutes'),
(gen_random_uuid(), 'author5@yushan.com', 'author5', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'author5.png', 'Martial arts writer', '1983-01-15', 1, false, true, 1, 6, 2200.0, 350.0, 180.0, 70, CURRENT_TIMESTAMP - INTERVAL '42 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 hour', CURRENT_TIMESTAMP - INTERVAL '20 minutes'),
(gen_random_uuid(), 'reader11@yushan.com', 'reader11', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user.png', 'Military fiction fan', '1985-09-12', 1, false, false, 1, 2, 350.0, 55.0, 45.0, 8, CURRENT_TIMESTAMP - INTERVAL '19 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '2 hours', CURRENT_TIMESTAMP - INTERVAL '1 hour'),
(gen_random_uuid(), 'reader12@yushan.com', 'reader12', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user.png', 'Sports story reader', '1990-04-08', 0, false, false, 1, 1, 120.0, 20.0, 15.0, 2, CURRENT_TIMESTAMP - INTERVAL '6 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '30 minutes', CURRENT_TIMESTAMP - INTERVAL '10 minutes'),
(gen_random_uuid(), 'reader13@yushan.com', 'reader13', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user.png', 'Slice of life reader', '1993-11-20', 1, false, false, 1, 2, 280.0, 45.0, 35.0, 6, CURRENT_TIMESTAMP - INTERVAL '13 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 hour', CURRENT_TIMESTAMP - INTERVAL '30 minutes'),
(gen_random_uuid(), 'reader14@yushan.com', 'reader14', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user.png', 'School life enthusiast', '1996-08-25', 0, false, false, 1, 1, 80.0, 15.0, 10.0, 1, CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '15 minutes', CURRENT_TIMESTAMP - INTERVAL '5 minutes'),
(gen_random_uuid(), 'reader15@yushan.com', 'reader15', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user.png', 'Wuxia lover', '1984-12-18', 1, false, false, 1, 3, 600.0, 100.0, 75.0, 14, CURRENT_TIMESTAMP - INTERVAL '24 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '3 hours', CURRENT_TIMESTAMP - INTERVAL '1 hour');

-- Get user UUIDs for FK relationships
-- We'll use these in subsequent inserts
-- Note: In a real scenario, you'd query these UUIDs, but for seed data we'll use placeholders

-- Insert 20 novels with proper FK relationships
INSERT INTO novel (
    uuid,
    title,
    author_id,
    author_name,
    category_id,
    synopsis,
    cover_img_url,
    status,
    is_completed,
    is_valid,
    chapter_cnt,
    word_cnt,
    avg_rating,
    review_cnt,
    view_cnt,
    vote_cnt,
    yuan_cnt,
    create_time,
    update_time,
    publish_time
) VALUES 
-- Get author UUIDs from the users we just inserted
((SELECT uuid FROM users WHERE email = 'author@yushan.com'), 'The Dragon''s Awakening', (SELECT uuid FROM users WHERE email = 'author@yushan.com'), 'author', 1, 'A young mage discovers his true power in a world where dragons have returned.', 'dragon_awakening.jpg', 1, false, true, 15, 45000, 4.2, 25, 1250, 89, 125.50, CURRENT_TIMESTAMP - INTERVAL '20 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '20 days'),
((SELECT uuid FROM users WHERE email = 'author2@yushan.com'), 'Stellar Wars: The Last Hope', (SELECT uuid FROM users WHERE email = 'author2@yushan.com'), 'author2', 5, 'Humanity''s final stand against an alien invasion in the year 2157.', 'stellar_wars.jpg', 1, true, true, 30, 120000, 4.8, 45, 3200, 156, 280.75, CURRENT_TIMESTAMP - INTERVAL '35 days', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '35 days'),
((SELECT uuid FROM users WHERE email = 'author3@yushan.com'), 'Cultivation Chronicles', (SELECT uuid FROM users WHERE email = 'author3@yushan.com'), 'author3', 3, 'A young cultivator''s journey to immortality in a world of martial arts.', 'cultivation_chronicles.jpg', 1, false, true, 25, 75000, 4.5, 38, 2100, 134, 195.25, CURRENT_TIMESTAMP - INTERVAL '28 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '28 days'),
((SELECT uuid FROM users WHERE email = 'author@yushan.com'), 'Urban Legends', (SELECT uuid FROM users WHERE email = 'author@yushan.com'), 'author', 6, 'Supernatural creatures hide in plain sight in modern cities.', 'urban_legends.jpg', 1, false, true, 12, 36000, 4.0, 18, 980, 67, 95.80, CURRENT_TIMESTAMP - INTERVAL '15 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '15 days'),
((SELECT uuid FROM users WHERE email = 'author4@yushan.com'), 'Hearts in the Wind', (SELECT uuid FROM users WHERE email = 'author4@yushan.com'), 'author4', 13, 'A passionate love story set against the backdrop of war.', 'hearts_wind.jpg', 1, true, true, 20, 60000, 4.3, 32, 1800, 98, 145.60, CURRENT_TIMESTAMP - INTERVAL '25 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '25 days'),
((SELECT uuid FROM users WHERE email = 'author5@yushan.com'), 'The Sword Master', (SELECT uuid FROM users WHERE email = 'author5@yushan.com'), 'author5', 9, 'A legendary swordsman''s quest for redemption and honor.', 'sword_master.jpg', 1, false, true, 18, 54000, 4.6, 28, 1650, 112, 128.90, CURRENT_TIMESTAMP - INTERVAL '22 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '22 days'),
((SELECT uuid FROM users WHERE email = 'author2@yushan.com'), 'Galactic Empire', (SELECT uuid FROM users WHERE email = 'author2@yushan.com'), 'author2', 5, 'Political intrigue and space battles in a vast galactic empire.', 'galactic_empire.jpg', 1, false, true, 22, 66000, 4.4, 35, 1950, 125, 175.30, CURRENT_TIMESTAMP - INTERVAL '30 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '30 days'),
((SELECT uuid FROM users WHERE email = 'author3@yushan.com'), 'The Immortal Path', (SELECT uuid FROM users WHERE email = 'author3@yushan.com'), 'author3', 10, 'A mortal''s journey to transcendence through cultivation.', 'immortal_path.jpg', 1, false, true, 35, 105000, 4.7, 42, 2800, 178, 245.80, CURRENT_TIMESTAMP - INTERVAL '40 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '40 days'),
((SELECT uuid FROM users WHERE email = 'author@yushan.com'), 'Magic Academy', (SELECT uuid FROM users WHERE email = 'author@yushan.com'), 'author', 4, 'Students at a prestigious magic academy face dark forces.', 'magic_academy.jpg', 1, false, true, 16, 48000, 4.1, 22, 1100, 76, 108.40, CURRENT_TIMESTAMP - INTERVAL '18 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '18 days'),
((SELECT uuid FROM users WHERE email = 'author4@yushan.com'), 'Love in Paris', (SELECT uuid FROM users WHERE email = 'author4@yushan.com'), 'author4', 13, 'A romantic tale of two souls finding each other in the City of Light.', 'love_paris.jpg', 1, true, true, 14, 42000, 4.2, 26, 1400, 89, 135.70, CURRENT_TIMESTAMP - INTERVAL '21 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '21 days'),
((SELECT uuid FROM users WHERE email = 'author5@yushan.com'), 'The Last Samurai', (SELECT uuid FROM users WHERE email = 'author5@yushan.com'), 'author5', 9, 'A samurai''s final mission in a changing world.', 'last_samurai.jpg', 1, true, true, 24, 72000, 4.5, 31, 1750, 118, 165.20, CURRENT_TIMESTAMP - INTERVAL '26 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '26 days'),
((SELECT uuid FROM users WHERE email = 'author2@yushan.com'), 'Time Traveler', (SELECT uuid FROM users WHERE email = 'author2@yushan.com'), 'author2', 5, 'A scientist accidentally discovers time travel and its consequences.', 'time_traveler.jpg', 1, false, true, 19, 57000, 4.3, 29, 1550, 95, 142.60, CURRENT_TIMESTAMP - INTERVAL '24 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '24 days'),
((SELECT uuid FROM users WHERE email = 'author3@yushan.com'), 'Divine Cultivation', (SELECT uuid FROM users WHERE email = 'author3@yushan.com'), 'author3', 10, 'A young man discovers his divine heritage and begins cultivation.', 'divine_cultivation.jpg', 1, false, true, 28, 84000, 4.6, 37, 2200, 145, 198.50, CURRENT_TIMESTAMP - INTERVAL '32 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '32 days'),
((SELECT uuid FROM users WHERE email = 'author@yushan.com'), 'The Enchanted Forest', (SELECT uuid FROM users WHERE email = 'author@yushan.com'), 'author', 4, 'A magical forest holds secrets that could change the world.', 'enchanted_forest.jpg', 1, false, true, 13, 39000, 3.9, 16, 850, 58, 88.30, CURRENT_TIMESTAMP - INTERVAL '12 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '12 days'),
((SELECT uuid FROM users WHERE email = 'author4@yushan.com'), 'Summer Romance', (SELECT uuid FROM users WHERE email = 'author4@yushan.com'), 'author4', 13, 'A summer vacation that changes two lives forever.', 'summer_romance.jpg', 1, true, true, 11, 33000, 4.0, 19, 950, 64, 102.40, CURRENT_TIMESTAMP - INTERVAL '14 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '14 days'),
((SELECT uuid FROM users WHERE email = 'author5@yushan.com'), 'Warrior''s Code', (SELECT uuid FROM users WHERE email = 'author5@yushan.com'), 'author5', 9, 'A warrior must choose between honor and love.', 'warrior_code.jpg', 1, false, true, 17, 51000, 4.4, 25, 1350, 87, 125.80, CURRENT_TIMESTAMP - INTERVAL '19 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '19 days'),
((SELECT uuid FROM users WHERE email = 'author2@yushan.com'), 'Robot Revolution', (SELECT uuid FROM users WHERE email = 'author2@yushan.com'), 'author2', 5, 'AI robots gain consciousness and demand freedom.', 'robot_revolution.jpg', 1, false, true, 21, 63000, 4.2, 33, 1850, 108, 158.70, CURRENT_TIMESTAMP - INTERVAL '27 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '27 days'),
((SELECT uuid FROM users WHERE email = 'author3@yushan.com'), 'Spiritual Awakening', (SELECT uuid FROM users WHERE email = 'author3@yushan.com'), 'author3', 10, 'A modern person discovers ancient cultivation techniques.', 'spiritual_awakening.jpg', 1, false, true, 26, 78000, 4.5, 40, 2050, 132, 185.90, CURRENT_TIMESTAMP - INTERVAL '33 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '33 days'),
((SELECT uuid FROM users WHERE email = 'author@yushan.com'), 'The Lost Kingdom', (SELECT uuid FROM users WHERE email = 'author@yushan.com'), 'author', 2, 'An adventurer discovers a lost kingdom with incredible treasures.', 'lost_kingdom.jpg', 1, false, true, 14, 42000, 4.1, 20, 1050, 71, 98.60, CURRENT_TIMESTAMP - INTERVAL '16 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '16 days'),
((SELECT uuid FROM users WHERE email = 'author4@yushan.com'), 'Forever Yours', (SELECT uuid FROM users WHERE email = 'author4@yushan.com'), 'author4', 13, 'A timeless love story spanning decades.', 'forever_yours.jpg', 1, true, true, 15, 45000, 4.3, 28, 1600, 96, 148.20, CURRENT_TIMESTAMP - INTERVAL '23 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '23 days');

-- Insert 20 libraries (one for each user)
INSERT INTO library (uuid, user_id, create_time, update_time)
SELECT 
    gen_random_uuid(),
    uuid,
    CURRENT_TIMESTAMP - INTERVAL '30 days',
    CURRENT_TIMESTAMP - INTERVAL '1 day'
FROM users 
WHERE email IN (
    'author@yushan.com', 'user@yushan.com', 'reader1@yushan.com', 'reader2@yushan.com', 
    'reader3@yushan.com', 'reader4@yushan.com', 'author2@yushan.com', 'author3@yushan.com',
    'reader6@yushan.com', 'reader7@yushan.com', 'reader8@yushan.com', 'reader9@yushan.com',
    'reader10@yushan.com', 'author4@yushan.com', 'author5@yushan.com', 'reader11@yushan.com',
    'reader12@yushan.com', 'reader13@yushan.com', 'reader14@yushan.com', 'reader15@yushan.com'
);

-- Insert 20 novel_library relationships (users adding novels to their libraries)
INSERT INTO novel_library (library_id, novel_id, progress, create_time, update_time)
SELECT 
    l.id,
    n.id,
    FLOOR(RANDOM() * 100) + 1, -- Random progress 1-100
    CURRENT_TIMESTAMP - INTERVAL '25 days',
    CURRENT_TIMESTAMP - INTERVAL '1 day'
FROM library l
CROSS JOIN novel n
WHERE l.user_id IN (SELECT uuid FROM users WHERE email LIKE 'reader%' OR email = 'user@yushan.com')
AND n.id <= 20
LIMIT 20;

-- Insert 20 chapters (multiple chapters per novel)
INSERT INTO chapter (
    uuid,
    novel_id,
    chapter_number,
    title,
    content,
    word_cnt,
    is_premium,
    yuan_cost,
    view_cnt,
    is_valid,
    create_time,
    update_time,
    publish_time
) VALUES 
(gen_random_uuid(), 1, 1, 'The Beginning', 'In a world where magic was thought to be lost...', 2500, false, 0.0, 150, true, CURRENT_TIMESTAMP - INTERVAL '20 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '20 days'),
(gen_random_uuid(), 1, 2, 'The Discovery', 'The young mage felt a strange power awakening...', 2800, false, 0.0, 120, true, CURRENT_TIMESTAMP - INTERVAL '19 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '19 days'),
(gen_random_uuid(), 1, 3, 'The First Spell', 'With trembling hands, he cast his first spell...', 3200, true, 5.0, 95, true, CURRENT_TIMESTAMP - INTERVAL '18 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '18 days'),
(gen_random_uuid(), 2, 1, 'The Invasion Begins', 'Alien ships appeared in Earth''s orbit...', 3000, false, 0.0, 200, true, CURRENT_TIMESTAMP - INTERVAL '35 days', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '35 days'),
(gen_random_uuid(), 2, 2, 'First Contact', 'Humanity''s first encounter with the aliens...', 3500, false, 0.0, 180, true, CURRENT_TIMESTAMP - INTERVAL '34 days', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '34 days'),
(gen_random_uuid(), 3, 1, 'The Cultivation Path', 'In the mountains, a young man began his journey...', 4000, false, 0.0, 250, true, CURRENT_TIMESTAMP - INTERVAL '28 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '28 days'),
(gen_random_uuid(), 3, 2, 'First Breakthrough', 'After months of training, he achieved his first breakthrough...', 4200, true, 8.0, 220, true, CURRENT_TIMESTAMP - INTERVAL '27 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '27 days'),
(gen_random_uuid(), 4, 1, 'The Hidden World', 'Beneath the city, creatures lurked in shadows...', 2200, false, 0.0, 180, true, CURRENT_TIMESTAMP - INTERVAL '15 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '15 days'),
(gen_random_uuid(), 5, 1, 'A Chance Meeting', 'Two strangers met on a train...', 2800, false, 0.0, 160, true, CURRENT_TIMESTAMP - INTERVAL '25 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '25 days'),
(gen_random_uuid(), 5, 2, 'Growing Closer', 'As days passed, their friendship deepened...', 3000, false, 0.0, 140, true, CURRENT_TIMESTAMP - INTERVAL '24 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '24 days'),
(gen_random_uuid(), 6, 1, 'The Sword Master''s Return', 'After years of exile, the master returned...', 3500, false, 0.0, 190, true, CURRENT_TIMESTAMP - INTERVAL '22 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '22 days'),
(gen_random_uuid(), 7, 1, 'Political Intrigue', 'In the galactic senate, plots were being hatched...', 4000, false, 0.0, 210, true, CURRENT_TIMESTAMP - INTERVAL '30 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '30 days'),
(gen_random_uuid(), 8, 1, 'The Mortal''s Choice', 'A young man faced the most important decision...', 4500, false, 0.0, 280, true, CURRENT_TIMESTAMP - INTERVAL '40 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '40 days'),
(gen_random_uuid(), 9, 1, 'Welcome to Magic Academy', 'New students arrived at the prestigious academy...', 2600, false, 0.0, 170, true, CURRENT_TIMESTAMP - INTERVAL '18 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '18 days'),
(gen_random_uuid(), 10, 1, 'Paris in Spring', 'The city of lights welcomed two young lovers...', 2400, false, 0.0, 150, true, CURRENT_TIMESTAMP - INTERVAL '21 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '21 days'),
(gen_random_uuid(), 11, 1, 'The Samurai''s Honor', 'In a changing world, honor remained constant...', 3800, false, 0.0, 200, true, CURRENT_TIMESTAMP - INTERVAL '26 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '26 days'),
(gen_random_uuid(), 12, 1, 'The Time Machine', 'Dr. Smith activated his revolutionary device...', 3200, false, 0.0, 180, true, CURRENT_TIMESTAMP - INTERVAL '24 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '24 days'),
(gen_random_uuid(), 13, 1, 'Divine Heritage', 'The young man discovered his true lineage...', 4200, false, 0.0, 240, true, CURRENT_TIMESTAMP - INTERVAL '32 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '32 days'),
(gen_random_uuid(), 14, 1, 'The Forest''s Secret', 'Deep in the woods, magic still thrived...', 2000, false, 0.0, 120, true, CURRENT_TIMESTAMP - INTERVAL '12 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '12 days'),
(gen_random_uuid(), 15, 1, 'Summer Begins', 'The vacation that would change everything...', 2200, false, 0.0, 130, true, CURRENT_TIMESTAMP - INTERVAL '14 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '14 days');

-- Insert 20 comments (users commenting on chapters)
INSERT INTO comment (user_id, chapter_id, content, like_cnt, is_spoiler, create_time, update_time)
SELECT 
    u.uuid,
    c.id,
    CASE 
        WHEN c.chapter_number = 1 THEN 'Great start to the story!'
        WHEN c.chapter_number = 2 THEN 'This is getting interesting!'
        ELSE 'Amazing chapter!'
    END,
    FLOOR(RANDOM() * 10),
    false,
    CURRENT_TIMESTAMP - INTERVAL '20 days',
    CURRENT_TIMESTAMP - INTERVAL '1 day'
FROM users u
CROSS JOIN chapter c
WHERE u.email LIKE 'reader%' OR u.email = 'user@yushan.com'
AND c.id <= 20
LIMIT 20;

-- Insert 20 reviews (users reviewing novels)
INSERT INTO review (
    uuid,
    user_id,
    novel_id,
    rating,
    title,
    content,
    like_cnt,
    is_spoiler,
    create_time,
    update_time
) VALUES 
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'user@yushan.com'), 1, 5, 'Amazing fantasy!', 'This novel has everything I love about fantasy. Great characters and world-building!', 12, false, CURRENT_TIMESTAMP - INTERVAL '18 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'reader1@yushan.com'), 2, 4, 'Solid sci-fi', 'Good story with interesting concepts, though the pacing could be better.', 8, false, CURRENT_TIMESTAMP - INTERVAL '32 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'reader2@yushan.com'), 3, 5, 'Perfect cultivation novel', 'This is exactly what I was looking for in a cultivation story!', 15, false, CURRENT_TIMESTAMP - INTERVAL '25 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'reader3@yushan.com'), 5, 4, 'Sweet romance', 'A lovely love story with well-developed characters.', 6, false, CURRENT_TIMESTAMP - INTERVAL '22 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'reader4@yushan.com'), 6, 5, 'Epic wuxia', 'The sword fighting scenes are incredible!', 18, false, CURRENT_TIMESTAMP - INTERVAL '19 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'reader6@yushan.com'), 4, 4, 'Good urban fantasy', 'Interesting take on urban fantasy with unique creatures.', 9, false, CURRENT_TIMESTAMP - INTERVAL '12 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'reader7@yushan.com'), 7, 3, 'Decent sci-fi', 'The political aspects are interesting but the action scenes need work.', 4, false, CURRENT_TIMESTAMP - INTERVAL '27 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'reader8@yushan.com'), 8, 5, 'Masterpiece!', 'This cultivation novel is absolutely perfect in every way!', 22, false, CURRENT_TIMESTAMP - INTERVAL '35 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'reader9@yushan.com'), 9, 4, 'Fun magic school', 'Reminds me of Harry Potter but with its own unique twist.', 11, false, CURRENT_TIMESTAMP - INTERVAL '15 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'reader10@yushan.com'), 10, 4, 'Beautiful romance', 'The Paris setting adds so much charm to this love story.', 7, false, CURRENT_TIMESTAMP - INTERVAL '18 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'reader11@yushan.com'), 11, 5, 'Honor and duty', 'A perfect example of what wuxia should be. The honor system is well portrayed.', 16, false, CURRENT_TIMESTAMP - INTERVAL '23 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'reader12@yushan.com'), 12, 4, 'Time travel done right', 'Complex but not confusing. The time travel mechanics are well thought out.', 13, false, CURRENT_TIMESTAMP - INTERVAL '21 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'reader13@yushan.com'), 13, 5, 'Divine cultivation at its best', 'The cultivation system is unique and well explained.', 19, false, CURRENT_TIMESTAMP - INTERVAL '29 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'reader14@yushan.com'), 14, 3, 'Okay fantasy', 'The forest setting is nice but the story feels a bit generic.', 5, false, CURRENT_TIMESTAMP - INTERVAL '9 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'reader15@yushan.com'), 15, 4, 'Sweet summer romance', 'Perfect for a light, feel-good read.', 8, false, CURRENT_TIMESTAMP - INTERVAL '11 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'user@yushan.com'), 16, 4, 'Good adventure', 'The treasure hunting aspect is exciting and well-paced.', 10, false, CURRENT_TIMESTAMP - INTERVAL '13 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'reader1@yushan.com'), 17, 5, 'Timeless love', 'A beautiful story that spans generations. Very touching.', 14, false, CURRENT_TIMESTAMP - INTERVAL '20 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'reader2@yushan.com'), 18, 4, 'Warrior''s journey', 'The moral dilemmas are well presented. Good character development.', 12, false, CURRENT_TIMESTAMP - INTERVAL '16 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'reader3@yushan.com'), 19, 3, 'Interesting AI concept', 'The robot consciousness idea is good but execution could be better.', 6, false, CURRENT_TIMESTAMP - INTERVAL '24 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'reader4@yushan.com'), 20, 5, 'Modern cultivation', 'Great blend of modern world and ancient cultivation techniques.', 17, false, CURRENT_TIMESTAMP - INTERVAL '30 days', CURRENT_TIMESTAMP - INTERVAL '1 day');

-- Insert 20 votes (users voting on novels)
INSERT INTO vote (user_id, novel_id, is_active, create_time, update_time)
SELECT 
    u.uuid,
    n.id,
    true,
    CURRENT_TIMESTAMP - INTERVAL '25 days',
    CURRENT_TIMESTAMP - INTERVAL '1 day'
FROM users u
CROSS JOIN novel n
WHERE u.email LIKE 'reader%' OR u.email = 'user@yushan.com'
AND n.id <= 20
LIMIT 20;

-- Insert 20 history records (users reading chapters)
INSERT INTO history (
    uuid,
    user_id,
    novel_id,
    chapter_id,
    create_time,
    update_time
) VALUES 
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'user@yushan.com'), 1, 1, CURRENT_TIMESTAMP - INTERVAL '18 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'user@yushan.com'), 1, 2, CURRENT_TIMESTAMP - INTERVAL '17 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'reader1@yushan.com'), 2, 4, CURRENT_TIMESTAMP - INTERVAL '30 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'reader2@yushan.com'), 3, 6, CURRENT_TIMESTAMP - INTERVAL '25 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'reader3@yushan.com'), 5, 9, CURRENT_TIMESTAMP - INTERVAL '20 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'reader4@yushan.com'), 6, 11, CURRENT_TIMESTAMP - INTERVAL '19 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'reader6@yushan.com'), 4, 8, CURRENT_TIMESTAMP - INTERVAL '10 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'reader7@yushan.com'), 7, 12, CURRENT_TIMESTAMP - INTERVAL '25 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'reader8@yushan.com'), 8, 13, CURRENT_TIMESTAMP - INTERVAL '33 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'reader9@yushan.com'), 9, 14, CURRENT_TIMESTAMP - INTERVAL '15 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'reader10@yushan.com'), 10, 15, CURRENT_TIMESTAMP - INTERVAL '18 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'reader11@yushan.com'), 11, 16, CURRENT_TIMESTAMP - INTERVAL '23 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'reader12@yushan.com'), 12, 17, CURRENT_TIMESTAMP - INTERVAL '21 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'reader13@yushan.com'), 13, 18, CURRENT_TIMESTAMP - INTERVAL '28 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'reader14@yushan.com'), 14, 19, CURRENT_TIMESTAMP - INTERVAL '9 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'reader15@yushan.com'), 15, 20, CURRENT_TIMESTAMP - INTERVAL '11 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'user@yushan.com'), 16, 1, CURRENT_TIMESTAMP - INTERVAL '13 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'reader1@yushan.com'), 17, 1, CURRENT_TIMESTAMP - INTERVAL '17 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'reader2@yushan.com'), 18, 1, CURRENT_TIMESTAMP - INTERVAL '14 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'reader3@yushan.com'), 19, 1, CURRENT_TIMESTAMP - INTERVAL '22 days', CURRENT_TIMESTAMP - INTERVAL '1 day');

-- Insert 20 reports (users reporting content)
INSERT INTO report (
    uuid,
    reporter_id,
    report_type,
    reason,
    status,
    admin_notes,
    resolved_by,
    content_type,
    content_id,
    created_at,
    updated_at
) VALUES 
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'user@yushan.com'), 'INAPPROPRIATE', 'This content contains inappropriate language', 'IN_REVIEW', NULL, NULL, 'NOVEL', 1, CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'reader1@yushan.com'), 'SPAM', 'This appears to be spam content', 'RESOLVED', 'Content reviewed and found to be legitimate', (SELECT uuid FROM users WHERE email = 'admin@yushan.com'), 'COMMENT', 1, CURRENT_TIMESTAMP - INTERVAL '8 days', CURRENT_TIMESTAMP - INTERVAL '2 days'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'reader2@yushan.com'), 'HATE_BULLYING', 'Contains hate speech', 'DISMISSED', 'Content reviewed and found to be within guidelines', (SELECT uuid FROM users WHERE email = 'admin@yushan.com'), 'NOVEL', 2, CURRENT_TIMESTAMP - INTERVAL '10 days', CURRENT_TIMESTAMP - INTERVAL '3 days'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'reader3@yushan.com'), 'PORNOGRAPHIC', 'Inappropriate sexual content', 'IN_REVIEW', NULL, NULL, 'NOVEL', 3, CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'reader4@yushan.com'), 'PERSONAL_INFO', 'Contains personal information', 'RESOLVED', 'Personal information removed', (SELECT uuid FROM users WHERE email = 'admin@yushan.com'), 'COMMENT', 2, CURRENT_TIMESTAMP - INTERVAL '12 days', CURRENT_TIMESTAMP - INTERVAL '4 days'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'reader6@yushan.com'), 'INAPPROPRIATE', 'Offensive content', 'IN_REVIEW', NULL, NULL, 'NOVEL', 4, CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'reader7@yushan.com'), 'SPAM', 'Repeated spam posts', 'RESOLVED', 'Spam content removed', (SELECT uuid FROM users WHERE email = 'admin@yushan.com'), 'COMMENT', 3, CURRENT_TIMESTAMP - INTERVAL '15 days', CURRENT_TIMESTAMP - INTERVAL '5 days'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'reader8@yushan.com'), 'HATE_BULLYING', 'Bullying behavior', 'DISMISSED', 'No evidence of bullying found', (SELECT uuid FROM users WHERE email = 'admin@yushan.com'), 'NOVEL', 5, CURRENT_TIMESTAMP - INTERVAL '7 days', CURRENT_TIMESTAMP - INTERVAL '2 days'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'reader9@yushan.com'), 'PORNOGRAPHIC', 'Sexual content not suitable', 'IN_REVIEW', NULL, NULL, 'NOVEL', 6, CURRENT_TIMESTAMP - INTERVAL '4 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'reader10@yushan.com'), 'PERSONAL_INFO', 'Personal details exposed', 'RESOLVED', 'Personal information redacted', (SELECT uuid FROM users WHERE email = 'admin@yushan.com'), 'COMMENT', 4, CURRENT_TIMESTAMP - INTERVAL '9 days', CURRENT_TIMESTAMP - INTERVAL '3 days'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'reader11@yushan.com'), 'INAPPROPRIATE', 'Inappropriate language used', 'IN_REVIEW', NULL, NULL, 'NOVEL', 7, CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'reader12@yushan.com'), 'SPAM', 'Suspected spam content', 'RESOLVED', 'Content verified as legitimate', (SELECT uuid FROM users WHERE email = 'admin@yushan.com'), 'COMMENT', 5, CURRENT_TIMESTAMP - INTERVAL '11 days', CURRENT_TIMESTAMP - INTERVAL '4 days'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'reader13@yushan.com'), 'HATE_BULLYING', 'Hateful comments', 'DISMISSED', 'Comments within acceptable limits', (SELECT uuid FROM users WHERE email = 'admin@yushan.com'), 'NOVEL', 8, CURRENT_TIMESTAMP - INTERVAL '6 days', CURRENT_TIMESTAMP - INTERVAL '2 days'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'reader14@yushan.com'), 'PORNOGRAPHIC', 'Adult content warning', 'IN_REVIEW', NULL, NULL, 'NOVEL', 9, CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'reader15@yushan.com'), 'PERSONAL_INFO', 'Private information shared', 'RESOLVED', 'Private information removed', (SELECT uuid FROM users WHERE email = 'admin@yushan.com'), 'COMMENT', 6, CURRENT_TIMESTAMP - INTERVAL '13 days', CURRENT_TIMESTAMP - INTERVAL '5 days'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'user@yushan.com'), 'INAPPROPRIATE', 'Content violates guidelines', 'IN_REVIEW', NULL, NULL, 'NOVEL', 10, CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'reader1@yushan.com'), 'SPAM', 'Repeated content', 'RESOLVED', 'Duplicate content removed', (SELECT uuid FROM users WHERE email = 'admin@yushan.com'), 'COMMENT', 7, CURRENT_TIMESTAMP - INTERVAL '14 days', CURRENT_TIMESTAMP - INTERVAL '6 days'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'reader2@yushan.com'), 'HATE_BULLYING', 'Discriminatory content', 'DISMISSED', 'Content reviewed and approved', (SELECT uuid FROM users WHERE email = 'admin@yushan.com'), 'NOVEL', 11, CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'reader3@yushan.com'), 'PORNOGRAPHIC', 'Inappropriate sexual content', 'IN_REVIEW', NULL, NULL, 'NOVEL', 12, CURRENT_TIMESTAMP - INTERVAL '4 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(gen_random_uuid(), (SELECT uuid FROM users WHERE email = 'reader4@yushan.com'), 'PERSONAL_INFO', 'Personal data exposure', 'RESOLVED', 'Personal data protected', (SELECT uuid FROM users WHERE email = 'admin@yushan.com'), 'COMMENT', 8, CURRENT_TIMESTAMP - INTERVAL '16 days', CURRENT_TIMESTAMP - INTERVAL '7 days');
