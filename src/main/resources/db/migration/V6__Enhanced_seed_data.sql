-- Enhanced seed data for Yushan Backend
-- This migration adds comprehensive test data with realistic relationships
-- 20 Authors + 30 Readers + 1 Admin = 51 total users
-- 200 novels distributed among authors
-- 500+ chapters distributed among novels
-- Realistic reviews, comments, votes, and history records

-- Insert 20 additional authors
INSERT INTO users (
    uuid, email, username, hash_password, email_verified, avatar_url,
    profile_detail, birthday, gender, is_admin, is_author,
    status, level, exp, yuan, read_time, read_book_num, create_time, update_time, last_login, last_active
) VALUES 
-- Author 1
(gen_random_uuid(), 'john.smith@yushan.com', 'johnsmith', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Fantasy and sci-fi author with 10+ years experience', '1985-03-15', 0, false, true, 1, 8, 2500.0, 400.0, 200.0, 80, CURRENT_TIMESTAMP - INTERVAL '60 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '2 hours', CURRENT_TIMESTAMP - INTERVAL '30 minutes'),
-- Author 2
(gen_random_uuid(), 'sarah.johnson@yushan.com', 'sarahjohnson', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Romance novelist specializing in contemporary love stories', '1988-07-22', 1, false, true, 1, 7, 2200.0, 350.0, 180.0, 70, CURRENT_TIMESTAMP - INTERVAL '55 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '3 hours', CURRENT_TIMESTAMP - INTERVAL '1 hour'),
-- Author 3
(gen_random_uuid(), 'mike.chen@yushan.com', 'mikechen', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Martial arts and cultivation expert', '1982-11-08', 0, false, true, 1, 9, 3000.0, 500.0, 250.0, 100, CURRENT_TIMESTAMP - INTERVAL '50 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 hour', CURRENT_TIMESTAMP - INTERVAL '20 minutes'),
-- Author 4
(gen_random_uuid(), 'emma.wilson@yushan.com', 'emmawilson', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Urban fantasy and paranormal romance writer', '1990-04-12', 1, false, true, 1, 6, 1800.0, 300.0, 150.0, 60, CURRENT_TIMESTAMP - INTERVAL '45 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '4 hours', CURRENT_TIMESTAMP - INTERVAL '2 hours'),
-- Author 5
(gen_random_uuid(), 'alex.rodriguez@yushan.com', 'alexrodriguez', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Sci-fi and space opera specialist', '1987-09-18', 0, false, true, 1, 8, 2600.0, 420.0, 210.0, 85, CURRENT_TIMESTAMP - INTERVAL '40 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '2 hours', CURRENT_TIMESTAMP - INTERVAL '45 minutes'),
-- Author 6
(gen_random_uuid(), 'lisa.brown@yushan.com', 'lisabrown', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Historical fiction and period drama author', '1984-12-03', 1, false, true, 1, 7, 2300.0, 380.0, 190.0, 75, CURRENT_TIMESTAMP - INTERVAL '35 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '3 hours', CURRENT_TIMESTAMP - INTERVAL '1 hour'),
-- Author 7
(gen_random_uuid(), 'david.kim@yushan.com', 'davidkim', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Mystery and thriller novelist', '1989-06-25', 0, false, true, 1, 6, 2000.0, 320.0, 160.0, 65, CURRENT_TIMESTAMP - INTERVAL '30 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 hour', CURRENT_TIMESTAMP - INTERVAL '30 minutes'),
-- Author 8
(gen_random_uuid(), 'jessica.taylor@yushan.com', 'jessicataylor', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Young adult fantasy and adventure writer', '1992-01-14', 1, false, true, 1, 5, 1500.0, 250.0, 125.0, 50, CURRENT_TIMESTAMP - INTERVAL '25 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '2 hours', CURRENT_TIMESTAMP - INTERVAL '1 hour'),
-- Author 9
(gen_random_uuid(), 'robert.garcia@yushan.com', 'robertgarcia', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Military fiction and war stories expert', '1981-08-30', 0, false, true, 1, 8, 2800.0, 450.0, 225.0, 90, CURRENT_TIMESTAMP - INTERVAL '20 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 hour', CURRENT_TIMESTAMP - INTERVAL '25 minutes'),
-- Author 10
(gen_random_uuid(), 'amanda.davis@yushan.com', 'amandadavis', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Contemporary romance and womens fiction', '1986-05-17', 1, false, true, 1, 6, 1900.0, 310.0, 155.0, 62, CURRENT_TIMESTAMP - INTERVAL '15 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '3 hours', CURRENT_TIMESTAMP - INTERVAL '1 hour'),
-- Author 11
(gen_random_uuid(), 'kevin.lee@yushan.com', 'kevinlee', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Cyberpunk and dystopian fiction writer', '1983-10-09', 0, false, true, 1, 7, 2400.0, 390.0, 195.0, 78, CURRENT_TIMESTAMP - INTERVAL '10 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '2 hours', CURRENT_TIMESTAMP - INTERVAL '40 minutes'),
-- Author 12
(gen_random_uuid(), 'michelle.white@yushan.com', 'michellewhite', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Paranormal romance and supernatural fiction', '1988-02-28', 1, false, true, 1, 6, 1700.0, 280.0, 140.0, 56, CURRENT_TIMESTAMP - INTERVAL '8 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '4 hours', CURRENT_TIMESTAMP - INTERVAL '2 hours'),
-- Author 13
(gen_random_uuid(), 'james.miller@yushan.com', 'jamesmiller', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Epic fantasy and high fantasy specialist', '1980-12-11', 0, false, true, 1, 9, 3200.0, 520.0, 260.0, 105, CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 hour', CURRENT_TIMESTAMP - INTERVAL '15 minutes'),
-- Author 14
(gen_random_uuid(), 'jennifer.anderson@yushan.com', 'jenniferanderson', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Slice of life and coming of age stories', '1991-07-06', 1, false, true, 1, 5, 1300.0, 220.0, 110.0, 44, CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '2 hours', CURRENT_TIMESTAMP - INTERVAL '1 hour'),
-- Author 15
(gen_random_uuid(), 'william.thomas@yushan.com', 'williamthomas', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Horror and psychological thriller author', '1985-04-23', 0, false, true, 1, 7, 2100.0, 340.0, 170.0, 68, CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '3 hours', CURRENT_TIMESTAMP - INTERVAL '1 hour'),
-- Author 16
(gen_random_uuid(), 'sophia.martinez@yushan.com', 'sophiamartinez', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'LGBTQ+ romance and diverse fiction', '1987-11-19', 1, false, true, 1, 6, 1800.0, 300.0, 150.0, 60, CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 hour', CURRENT_TIMESTAMP - INTERVAL '30 minutes'),
-- Author 17
(gen_random_uuid(), 'daniel.jackson@yushan.com', 'danieljackson', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Adventure and exploration fiction', '1984-03-07', 0, false, true, 1, 7, 2500.0, 410.0, 205.0, 82, CURRENT_TIMESTAMP - INTERVAL '12 hours', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '2 hours', CURRENT_TIMESTAMP - INTERVAL '1 hour'),
-- Author 18
(gen_random_uuid(), 'olivia.harris@yushan.com', 'oliviaharris', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Literary fiction and character-driven stories', '1989-09-15', 1, false, true, 1, 6, 1600.0, 270.0, 135.0, 54, CURRENT_TIMESTAMP - INTERVAL '6 hours', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 hour', CURRENT_TIMESTAMP - INTERVAL '45 minutes'),
-- Author 19
(gen_random_uuid(), 'christopher.moore@yushan.com', 'christophermoore', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Comedy and satirical fiction', '1982-06-12', 0, false, true, 1, 6, 1400.0, 230.0, 115.0, 46, CURRENT_TIMESTAMP - INTERVAL '3 hours', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '30 minutes', CURRENT_TIMESTAMP - INTERVAL '15 minutes'),
-- Author 20
(gen_random_uuid(), 'natalie.clark@yushan.com', 'natalieclark', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Magical realism and surreal fiction', '1986-08-04', 1, false, true, 1, 5, 1200.0, 200.0, 100.0, 40, CURRENT_TIMESTAMP - INTERVAL '1 hour', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '15 minutes', CURRENT_TIMESTAMP - INTERVAL '5 minutes');

-- Insert readers
INSERT INTO users (
    uuid, email, username, hash_password, email_verified, avatar_url,
    profile_detail, birthday, gender, is_admin, is_author,
    status, level, exp, yuan, read_time, read_book_num, create_time, update_time, last_login, last_active
) VALUES 
(gen_random_uuid(), 'alice.johnson@yushan.com', 'alicejohnson', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Fantasy enthusiast and avid reader', '1995-03-15', 1, false, false, 1, 3, 600.0, 100.0, 50.0, 20, CURRENT_TIMESTAMP - INTERVAL '45 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '2 hours', CURRENT_TIMESTAMP - INTERVAL '1 hour'),
(gen_random_uuid(), 'bob.smith@yushan.com', 'bobsmith', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Sci-fi lover and technology enthusiast', '1992-07-22', 0, false, false, 1, 4, 800.0, 130.0, 65.0, 25, CURRENT_TIMESTAMP - INTERVAL '40 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '3 hours', CURRENT_TIMESTAMP - INTERVAL '2 hours'),
(gen_random_uuid(), 'carol.williams@yushan.com', 'carolwilliams', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Romance reader and book club member', '1998-11-08', 1, false, false, 1, 2, 400.0, 70.0, 35.0, 15, CURRENT_TIMESTAMP - INTERVAL '35 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 hour', CURRENT_TIMESTAMP - INTERVAL '30 minutes'),
(gen_random_uuid(), 'david.brown@yushan.com', 'davidbrown', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Martial arts fan and action lover', '1990-04-12', 0, false, false, 1, 5, 1000.0, 160.0, 80.0, 30, CURRENT_TIMESTAMP - INTERVAL '30 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '4 hours', CURRENT_TIMESTAMP - INTERVAL '1 hour'),
(gen_random_uuid(), 'eve.davis@yushan.com', 'evedavis', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Urban fantasy reader and night owl', '1993-09-18', 1, false, false, 1, 3, 700.0, 110.0, 55.0, 22, CURRENT_TIMESTAMP - INTERVAL '25 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '2 hours', CURRENT_TIMESTAMP - INTERVAL '1 hour'),
(gen_random_uuid(), 'frank.miller@yushan.com', 'frankmiller', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Historical fiction enthusiast', '1987-12-03', 0, false, false, 1, 4, 900.0, 140.0, 70.0, 28, CURRENT_TIMESTAMP - INTERVAL '20 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '3 hours', CURRENT_TIMESTAMP - INTERVAL '1 hour'),
(gen_random_uuid(), 'grace.wilson@yushan.com', 'gracewilson', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Young adult fiction lover', '1996-06-25', 1, false, false, 1, 2, 500.0, 80.0, 40.0, 18, CURRENT_TIMESTAMP - INTERVAL '15 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 hour', CURRENT_TIMESTAMP - INTERVAL '30 minutes'),
(gen_random_uuid(), 'henry.taylor@yushan.com', 'henrytaylor', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Mystery and thriller reader', '1991-01-14', 0, false, false, 1, 3, 650.0, 105.0, 52.0, 21, CURRENT_TIMESTAMP - INTERVAL '10 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '2 hours', CURRENT_TIMESTAMP - INTERVAL '1 hour'),
(gen_random_uuid(), 'iris.garcia@yushan.com', 'irisgarcia', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Paranormal romance fan', '1994-08-30', 1, false, false, 1, 3, 750.0, 120.0, 60.0, 24, CURRENT_TIMESTAMP - INTERVAL '8 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 hour', CURRENT_TIMESTAMP - INTERVAL '45 minutes'),
(gen_random_uuid(), 'jack.anderson@yushan.com', 'jackanderson', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Military fiction enthusiast', '1989-05-17', 0, false, false, 1, 4, 850.0, 135.0, 67.0, 27, CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '3 hours', CURRENT_TIMESTAMP - INTERVAL '2 hours'),
(gen_random_uuid(), 'karen.lee@yushan.com', 'karenlee', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Contemporary romance reader', '1997-10-09', 1, false, false, 1, 2, 450.0, 75.0, 37.0, 16, CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '30 minutes', CURRENT_TIMESTAMP - INTERVAL '15 minutes'),
(gen_random_uuid(), 'leo.white@yushan.com', 'leowhite', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Cyberpunk and dystopian fiction fan', '1992-02-28', 0, false, false, 1, 4, 800.0, 130.0, 65.0, 25, CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '2 hours', CURRENT_TIMESTAMP - INTERVAL '1 hour'),
(gen_random_uuid(), 'mary.martinez@yushan.com', 'marymartinez', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Epic fantasy reader', '1995-12-11', 1, false, false, 1, 3, 700.0, 110.0, 55.0, 22, CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 hour', CURRENT_TIMESTAMP - INTERVAL '30 minutes'),
(gen_random_uuid(), 'nick.thomas@yushan.com', 'nickthomas', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Horror and thriller enthusiast', '1990-07-06', 0, false, false, 1, 4, 900.0, 140.0, 70.0, 28, CURRENT_TIMESTAMP - INTERVAL '12 hours', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '3 hours', CURRENT_TIMESTAMP - INTERVAL '1 hour'),
(gen_random_uuid(), 'olivia.jackson@yushan.com', 'oliviajackson', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Slice of life and drama reader', '1993-04-23', 1, false, false, 1, 3, 600.0, 100.0, 50.0, 20, CURRENT_TIMESTAMP - INTERVAL '6 hours', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '2 hours', CURRENT_TIMESTAMP - INTERVAL '1 hour'),
(gen_random_uuid(), 'peter.harris@yushan.com', 'peterharris', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Adventure and exploration fiction fan', '1988-11-19', 0, false, false, 1, 4, 950.0, 150.0, 75.0, 30, CURRENT_TIMESTAMP - INTERVAL '3 hours', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 hour', CURRENT_TIMESTAMP - INTERVAL '30 minutes'),
(gen_random_uuid(), 'quinn.moore@yushan.com', 'quinnmoore', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Literary fiction and character-driven stories', '1991-03-07', 1, false, false, 1, 3, 650.0, 105.0, 52.0, 21, CURRENT_TIMESTAMP - INTERVAL '1 hour', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '30 minutes', CURRENT_TIMESTAMP - INTERVAL '15 minutes'),
(gen_random_uuid(), 'ryan.clark@yushan.com', 'ryanclark', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Comedy and satirical fiction reader', '1994-09-15', 0, false, false, 1, 3, 550.0, 90.0, 45.0, 18, CURRENT_TIMESTAMP - INTERVAL '30 minutes', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '15 minutes', CURRENT_TIMESTAMP - INTERVAL '5 minutes'),
(gen_random_uuid(), 'sophia.lewis@yushan.com', 'sophialewis', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Magical realism and surreal fiction lover', '1996-06-12', 1, false, false, 1, 2, 400.0, 65.0, 32.0, 14, CURRENT_TIMESTAMP - INTERVAL '15 minutes', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '10 minutes', CURRENT_TIMESTAMP - INTERVAL '2 minutes'),
(gen_random_uuid(), 'tom.walker@yushan.com', 'tomwalker', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Science fiction and space opera fan', '1989-08-04', 0, false, false, 1, 4, 850.0, 135.0, 67.0, 27, CURRENT_TIMESTAMP - INTERVAL '5 minutes', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '2 minutes', CURRENT_TIMESTAMP - INTERVAL '1 minute'),
(gen_random_uuid(), 'una.martin@yushan.com', 'unamartin', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Fantasy romance and adventure reader', '1992-11-30', 1, false, false, 1, 3, 750.0, 120.0, 60.0, 24, CURRENT_TIMESTAMP - INTERVAL '2 minutes', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 minute', CURRENT_TIMESTAMP - INTERVAL '30 seconds'),
(gen_random_uuid(), 'victor.nguyen@yushan.com', 'victornguyen', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Martial arts and cultivation enthusiast', '1987-05-14', 0, false, false, 1, 4, 900.0, 140.0, 70.0, 28, CURRENT_TIMESTAMP - INTERVAL '1 minute', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '30 seconds', CURRENT_TIMESTAMP - INTERVAL '10 seconds'),
(gen_random_uuid(), 'wendy.king@yushan.com', 'wendyking', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Urban fantasy and paranormal romance lover', '1994-09-22', 1, false, false, 1, 3, 650.0, 105.0, 52.0, 21, CURRENT_TIMESTAMP - INTERVAL '30 seconds', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '15 seconds', CURRENT_TIMESTAMP - INTERVAL '5 seconds'),
(gen_random_uuid(), 'xavier.lopez@yushan.com', 'xavierlopez', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Historical fiction and period drama fan', '1986-12-08', 0, false, false, 1, 4, 800.0, 130.0, 65.0, 25, CURRENT_TIMESTAMP - INTERVAL '15 seconds', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '10 seconds', CURRENT_TIMESTAMP - INTERVAL '3 seconds'),
(gen_random_uuid(), 'yara.singh@yushan.com', 'yarasingh', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Contemporary romance and womens fiction reader', '1991-03-18', 1, false, false, 1, 3, 600.0, 100.0, 50.0, 20, CURRENT_TIMESTAMP - INTERVAL '10 seconds', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '5 seconds', CURRENT_TIMESTAMP - INTERVAL '2 seconds'),
(gen_random_uuid(), 'zachary.wright@yushan.com', 'zacharywright', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Mystery and thriller enthusiast', '1989-07-25', 0, false, false, 1, 4, 850.0, 135.0, 67.0, 27, CURRENT_TIMESTAMP - INTERVAL '5 seconds', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '3 seconds', CURRENT_TIMESTAMP - INTERVAL '1 second'),
(gen_random_uuid(), 'alice.johnson@gmail.com', 'alicejohnson', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Book lover and avid reader', '1995-03-15', 1, false, false, 1, 3, 800.0, 120.0, 60.0, 25, CURRENT_TIMESTAMP - INTERVAL '45 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '1 hour'),
(gen_random_uuid(), 'bob.smith@yahoo.com', 'bobsmith', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Fantasy and sci-fi enthusiast', '1988-07-22', 0, false, false, 1, 4, 1200.0, 180.0, 90.0, 35, CURRENT_TIMESTAMP - INTERVAL '40 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '30 minutes'),
(gen_random_uuid(), 'carol.williams@hotmail.com', 'carolwilliams', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Romance novel reader', '1992-11-08', 1, false, false, 1, 2, 600.0, 90.0, 45.0, 18, CURRENT_TIMESTAMP - INTERVAL '35 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '2 hours'),
(gen_random_uuid(), 'david.brown@outlook.com', 'davidbrown', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Mystery and thriller fan', '1987-04-12', 0, false, false, 1, 5, 1500.0, 220.0, 110.0, 42, CURRENT_TIMESTAMP - INTERVAL '30 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '45 minutes'),
(gen_random_uuid(), 'emma.davis@gmail.com', 'emmadavis', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Historical fiction lover', '1990-09-18', 1, false, false, 1, 3, 900.0, 135.0, 67.0, 28, CURRENT_TIMESTAMP - INTERVAL '25 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '1 hour'),
(gen_random_uuid(), 'frank.miller@yahoo.com', 'frankmiller', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Adventure and action reader', '1985-12-03', 0, false, false, 1, 4, 1100.0, 165.0, 82.0, 32, CURRENT_TIMESTAMP - INTERVAL '20 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '15 minutes'),
(gen_random_uuid(), 'grace.wilson@hotmail.com', 'gracewilson', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Young adult fiction reader', '1993-06-25', 1, false, false, 1, 2, 700.0, 105.0, 52.0, 21, CURRENT_TIMESTAMP - INTERVAL '15 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '2 hours'),
(gen_random_uuid(), 'henry.moore@outlook.com', 'henrymoore', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Science fiction enthusiast', '1989-01-14', 0, false, false, 1, 5, 1300.0, 195.0, 97.0, 38, CURRENT_TIMESTAMP - INTERVAL '10 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '30 minutes'),
(gen_random_uuid(), 'iris.taylor@gmail.com', 'iristaylor', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Literary fiction reader', '1991-08-30', 1, false, false, 1, 3, 850.0, 127.0, 63.0, 26, CURRENT_TIMESTAMP - INTERVAL '8 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '1 hour'),
(gen_random_uuid(), 'jack.anderson@yahoo.com', 'jackanderson', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Horror and supernatural fan', '1986-05-17', 0, false, false, 1, 4, 1000.0, 150.0, 75.0, 30, CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '20 minutes'),
(gen_random_uuid(), 'karen.thomas@hotmail.com', 'karenthomas', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Contemporary romance reader', '1994-10-09', 1, false, false, 1, 2, 650.0, 97.0, 48.0, 19, CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '1 hour'),
(gen_random_uuid(), 'leo.jackson@outlook.com', 'leojackson', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Military and war fiction', '1983-02-28', 0, false, false, 1, 5, 1400.0, 210.0, 105.0, 40, CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '25 minutes'),
(gen_random_uuid(), 'mary.white@gmail.com', 'marywhite', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Urban fantasy lover', '1996-12-11', 1, false, false, 1, 3, 750.0, 112.0, 56.0, 23, CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '40 minutes'),
(gen_random_uuid(), 'nick.harris@yahoo.com', 'nickharris', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Cyberpunk and dystopian reader', '1988-07-06', 0, false, false, 1, 4, 1150.0, 172.0, 86.0, 34, CURRENT_TIMESTAMP - INTERVAL '12 hours', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '10 minutes'),
(gen_random_uuid(), 'olivia.martin@hotmail.com', 'oliviamartin', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Paranormal romance reader', '1992-04-23', 1, false, false, 1, 3, 800.0, 120.0, 60.0, 25, CURRENT_TIMESTAMP - INTERVAL '6 hours', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '1 hour'),
(gen_random_uuid(), 'paul.garcia@outlook.com', 'paulgarcia', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Epic fantasy enthusiast', '1987-11-19', 0, false, false, 1, 5, 1350.0, 202.0, 101.0, 39, CURRENT_TIMESTAMP - INTERVAL '3 hours', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '5 minutes'),
(gen_random_uuid(), 'quinn.lee@gmail.com', 'quinnlee', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Slice of life stories', '1995-03-07', 1, false, false, 1, 2, 600.0, 90.0, 45.0, 18, CURRENT_TIMESTAMP - INTERVAL '1 hour', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '15 minutes'),
(gen_random_uuid(), 'ryan.clark@yahoo.com', 'ryanclark', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Psychological thriller fan', '1989-09-15', 0, false, false, 1, 4, 1050.0, 157.0, 78.0, 31, CURRENT_TIMESTAMP - INTERVAL '30 minutes', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '2 minutes'),
(gen_random_uuid(), 'sophia.rodriguez@hotmail.com', 'sophiarodriguez', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'LGBTQ+ fiction reader', '1993-06-25', 1, false, false, 1, 3, 850.0, 127.0, 63.0, 26, CURRENT_TIMESTAMP - INTERVAL '15 minutes', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 minute'),
(gen_random_uuid(), 'tom.walker@outlook.com', 'tomwalker', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Adventure and exploration', '1985-01-14', 0, false, false, 1, 4, 1200.0, 180.0, 90.0, 35, CURRENT_TIMESTAMP - INTERVAL '5 minutes', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '30 seconds'),
(gen_random_uuid(), 'anna.kim@gmail.com', 'annakim', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Korean drama and romance reader', '1994-05-20', 1, false, false, 1, 3, 750.0, 112.0, 56.0, 23, CURRENT_TIMESTAMP - INTERVAL '50 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '1 hour'),
(gen_random_uuid(), 'ben.chen@yahoo.com', 'benchen', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Martial arts and cultivation', '1987-08-15', 0, false, false, 1, 4, 1100.0, 165.0, 82.0, 32, CURRENT_TIMESTAMP - INTERVAL '48 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '45 minutes'),
(gen_random_uuid(), 'carla.silva@hotmail.com', 'carlasilva', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Latin American literature', '1991-12-03', 1, false, false, 1, 3, 900.0, 135.0, 67.0, 28, CURRENT_TIMESTAMP - INTERVAL '45 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '2 hours'),
(gen_random_uuid(), 'diego.rodriguez@outlook.com', 'diegorodriguez', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Spanish literature enthusiast', '1989-03-18', 0, false, false, 1, 4, 1250.0, 187.0, 93.0, 36, CURRENT_TIMESTAMP - INTERVAL '42 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '30 minutes'),
(gen_random_uuid(), 'elena.petrov@gmail.com', 'elenapetrov', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Russian literature reader', '1993-07-25', 1, false, false, 1, 3, 850.0, 127.0, 63.0, 26, CURRENT_TIMESTAMP - INTERVAL '40 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '1 hour'),
(gen_random_uuid(), 'frank.zhang@yahoo.com', 'frankzhang', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Chinese web novels', '1986-11-12', 0, false, false, 1, 5, 1400.0, 210.0, 105.0, 40, CURRENT_TIMESTAMP - INTERVAL '38 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '20 minutes'),
(gen_random_uuid(), 'grace.nguyen@hotmail.com', 'gracenguyen', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Vietnamese literature', '1992-04-08', 1, false, false, 1, 3, 800.0, 120.0, 60.0, 25, CURRENT_TIMESTAMP - INTERVAL '35 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '1 hour'),
(gen_random_uuid(), 'harry.singh@outlook.com', 'harrysingh', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Indian mythology and fantasy', '1988-09-30', 0, false, false, 1, 4, 1150.0, 172.0, 86.0, 34, CURRENT_TIMESTAMP - INTERVAL '32 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '15 minutes'),
(gen_random_uuid(), 'isabella.torres@gmail.com', 'isabellatorres', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Mexican literature', '1995-01-22', 1, false, false, 1, 2, 650.0, 97.0, 48.0, 19, CURRENT_TIMESTAMP - INTERVAL '30 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '2 hours'),
(gen_random_uuid(), 'james.oconnor@yahoo.com', 'jamesoconnor', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Irish literature', '1984-06-14', 0, false, false, 1, 5, 1300.0, 195.0, 97.0, 38, CURRENT_TIMESTAMP - INTERVAL '28 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '25 minutes'),
(gen_random_uuid(), 'kate.murphy@hotmail.com', 'katemurphy', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Australian literature', '1990-10-07', 1, false, false, 1, 3, 900.0, 135.0, 67.0, 28, CURRENT_TIMESTAMP - INTERVAL '25 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '1 hour'),
(gen_random_uuid(), 'liam.oconnell@outlook.com', 'liamoconnell', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Celtic mythology', '1987-02-19', 0, false, false, 1, 4, 1050.0, 157.0, 78.0, 31, CURRENT_TIMESTAMP - INTERVAL '22 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '10 minutes'),
(gen_random_uuid(), 'maya.patel@gmail.com', 'mayapatel', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'South Asian literature', '1993-05-26', 1, false, false, 1, 3, 850.0, 127.0, 63.0, 26, CURRENT_TIMESTAMP - INTERVAL '20 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '1 hour'),
(gen_random_uuid(), 'nathan.wong@yahoo.com', 'nathanwong', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Hong Kong literature', '1989-08-11', 0, false, false, 1, 4, 1200.0, 180.0, 90.0, 35, CURRENT_TIMESTAMP - INTERVAL '18 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '35 minutes'),
(gen_random_uuid(), 'olivia.kim@hotmail.com', 'oliviakim', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Korean web novels', '1996-12-28', 1, false, false, 1, 2, 700.0, 105.0, 52.0, 21, CURRENT_TIMESTAMP - INTERVAL '15 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '2 hours'),
(gen_random_uuid(), 'peter.anderson@outlook.com', 'peteranderson', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Scandinavian noir', '1985-03-05', 0, false, false, 1, 5, 1350.0, 202.0, 101.0, 39, CURRENT_TIMESTAMP - INTERVAL '12 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '5 minutes'),
(gen_random_uuid(), 'quinn.martinez@gmail.com', 'quinnmartinez', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Latinx literature', '1991-07-17', 1, false, false, 1, 3, 800.0, 120.0, 60.0, 25, CURRENT_TIMESTAMP - INTERVAL '10 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '1 hour'),
(gen_random_uuid(), 'robert.kim@yahoo.com', 'robertkim', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Korean fantasy', '1988-11-23', 0, false, false, 1, 4, 1100.0, 165.0, 82.0, 32, CURRENT_TIMESTAMP - INTERVAL '8 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '30 minutes'),
(gen_random_uuid(), 'sarah.johnson@hotmail.com', 'sarahjohnson2', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'British literature', '1994-04-16', 1, false, false, 1, 3, 900.0, 135.0, 67.0, 28, CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '1 hour'),
(gen_random_uuid(), 'thomas.lee@outlook.com', 'thomaslee', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Chinese literature', '1986-09-02', 0, false, false, 1, 4, 1250.0, 187.0, 93.0, 36, CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '15 minutes'),
(gen_random_uuid(), 'una.oconnor@gmail.com', 'unaoconnor', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Irish folklore', '1992-01-13', 1, false, false, 1, 3, 850.0, 127.0, 63.0, 26, CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '45 minutes'),
(gen_random_uuid(), 'victor.nguyen@yahoo.com', 'victornguyen', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Vietnamese fantasy', '1989-06-09', 0, false, false, 1, 4, 1150.0, 172.0, 86.0, 34, CURRENT_TIMESTAMP - INTERVAL '12 hours', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '10 minutes'),
(gen_random_uuid(), 'wendy.chen@hotmail.com', 'wendychen', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Chinese romance', '1995-10-31', 1, false, false, 1, 2, 700.0, 105.0, 52.0, 21, CURRENT_TIMESTAMP - INTERVAL '6 hours', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '1 hour'),
(gen_random_uuid(), 'xavier.rodriguez@outlook.com', 'xavierrodriguez', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Spanish fantasy', '1987-03-27', 0, false, false, 1, 5, 1300.0, 195.0, 97.0, 38, CURRENT_TIMESTAMP - INTERVAL '3 hours', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '5 minutes'),
(gen_random_uuid(), 'yuki.tanaka@gmail.com', 'yukitanaka', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Japanese literature', '1993-08-14', 1, false, false, 1, 3, 900.0, 135.0, 67.0, 28, CURRENT_TIMESTAMP - INTERVAL '1 hour', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '30 minutes'),
(gen_random_uuid(), 'zachary.wong@yahoo.com', 'zacharywong', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Cantonese literature', '1988-12-01', 0, false, false, 1, 4, 1200.0, 180.0, 90.0, 35, CURRENT_TIMESTAMP - INTERVAL '30 minutes', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '2 minutes'),
(gen_random_uuid(), 'alexandra.smith@hotmail.com', 'alexandrasmith', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Canadian literature', '1994-05-18', 1, false, false, 1, 3, 850.0, 127.0, 63.0, 26, CURRENT_TIMESTAMP - INTERVAL '15 minutes', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 minute'),
(gen_random_uuid(), 'brandon.kim@outlook.com', 'brandonkim', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Korean sci-fi', '1986-10-25', 0, false, false, 1, 4, 1100.0, 165.0, 82.0, 32, CURRENT_TIMESTAMP - INTERVAL '5 minutes', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '30 seconds'),
(gen_random_uuid(), 'catherine.davis@gmail.com', 'catherinedavis', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Australian romance', '1991-02-12', 1, false, false, 1, 3, 800.0, 120.0, 60.0, 25, CURRENT_TIMESTAMP - INTERVAL '2 minutes', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '15 seconds'),
(gen_random_uuid(), 'daniel.patel@yahoo.com', 'danielpatel', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Indian sci-fi', '1989-07-08', 0, false, false, 1, 4, 1250.0, 187.0, 93.0, 36, CURRENT_TIMESTAMP - INTERVAL '1 minute', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '5 seconds'),
(gen_random_uuid(), 'elena.garcia@hotmail.com', 'elenagarcia', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Mexican fantasy', '1996-11-21', 1, false, false, 1, 2, 650.0, 97.0, 48.0, 19, CURRENT_TIMESTAMP - INTERVAL '30 seconds', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 second'),
(gen_random_uuid(), 'felix.lee@outlook.com', 'felixlee', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Chinese cultivation', '1987-04-03', 0, false, false, 1, 5, 1400.0, 210.0, 105.0, 40, CURRENT_TIMESTAMP - INTERVAL '10 seconds', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 second'),
(gen_random_uuid(), 'gina.martinez@gmail.com', 'ginamartinez', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Latinx romance', '1993-09-16', 1, false, false, 1, 3, 900.0, 135.0, 67.0, 28, CURRENT_TIMESTAMP - INTERVAL '5 seconds', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 second'),
(gen_random_uuid(), 'hugo.silva@yahoo.com', 'hugosilva', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Brazilian literature', '1985-01-29', 0, false, false, 1, 4, 1150.0, 172.0, 86.0, 34, CURRENT_TIMESTAMP - INTERVAL '2 seconds', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 second'),
(gen_random_uuid(), 'iris.wang@hotmail.com', 'iriswang', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Chinese romance', '1992-06-05', 1, false, false, 1, 3, 800.0, 120.0, 60.0, 25, CURRENT_TIMESTAMP - INTERVAL '1 second', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 second'),
(gen_random_uuid(), 'jason.kim@outlook.com', 'jasonkim', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Korean thriller', '1988-12-17', 0, false, false, 1, 4, 1200.0, 180.0, 90.0, 35, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP),
(gen_random_uuid(), 'kelly.zhang@gmail.com', 'kellyzhang', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Chinese fantasy', '1995-03-24', 1, false, false, 1, 2, 700.0, 105.0, 52.0, 21, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP),
(gen_random_uuid(), 'liam.oconnell@yahoo.com', 'liamoconnell2', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Irish mythology', '1984-08-11', 0, false, false, 1, 5, 1300.0, 195.0, 97.0, 38, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP),
(gen_random_uuid(), 'maya.singh@hotmail.com', 'mayasingh', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Indian romance', '1990-11-28', 1, false, false, 1, 3, 850.0, 127.0, 63.0, 26, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP),
(gen_random_uuid(), 'nathan.torres@outlook.com', 'nathantorres', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Mexican thriller', '1987-05-15', 0, false, false, 1, 4, 1100.0, 165.0, 82.0, 32, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP),
(gen_random_uuid(), 'olivia.nguyen@gmail.com', 'olivianguyen', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Vietnamese romance', '1993-10-02', 1, false, false, 1, 3, 900.0, 135.0, 67.0, 28, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP),
(gen_random_uuid(), 'peter.chen@yahoo.com', 'peterchen', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Chinese sci-fi', '1986-01-19', 0, false, false, 1, 4, 1250.0, 187.0, 93.0, 36, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP),
(gen_random_uuid(), 'quinn.murphy@hotmail.com', 'quinnmurphy', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Irish romance', '1994-07-06', 1, false, false, 1, 2, 650.0, 97.0, 48.0, 19, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP),
(gen_random_uuid(), 'ryan.kim@outlook.com', 'ryankim', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Korean fantasy', '1989-12-13', 0, false, false, 1, 5, 1400.0, 210.0, 105.0, 40, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP),
(gen_random_uuid(), 'sophia.lee@gmail.com', 'sophialee', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Chinese romance', '1991-04-20', 1, false, false, 1, 3, 800.0, 120.0, 60.0, 25, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP),
(gen_random_uuid(), 'thomas.rodriguez@yahoo.com', 'thomasrodriguez', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Spanish thriller', '1985-09-27', 0, false, false, 1, 4, 1150.0, 172.0, 86.0, 34, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP),
(gen_random_uuid(), 'una.patel@hotmail.com', 'unapatel', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Indian fantasy', '1992-02-14', 1, false, false, 1, 3, 900.0, 135.0, 67.0, 28, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP),
(gen_random_uuid(), 'victor.wong@outlook.com', 'victorwong', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Hong Kong thriller', '1988-06-01', 0, false, false, 1, 4, 1200.0, 180.0, 90.0, 35, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP),
(gen_random_uuid(), 'wendy.silva@gmail.com', 'wendysilva', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Brazilian romance', '1995-11-18', 1, false, false, 1, 2, 700.0, 105.0, 52.0, 21, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP),
(gen_random_uuid(), 'xavier.garcia@yahoo.com', 'xaviergarcia', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Mexican fantasy', '1987-03-25', 0, false, false, 1, 5, 1350.0, 202.0, 101.0, 39, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP),
(gen_random_uuid(), 'yuki.tanaka@hotmail.com', 'yukitanaka2', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_female.png', 'Japanese romance', '1993-08-12', 1, false, false, 1, 3, 850.0, 127.0, 63.0, 26, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP),
(gen_random_uuid(), 'zachary.lee@outlook.com', 'zacharylee', '$2a$10$VXLDKUp3hDHJn9a17ZNsI.ZJZi.bWjwqLrkjEIug4pHGt1OsUGR5G', true, 'user_male.png', 'Korean thriller', '1989-12-29', 0, false, false, 1, 4, 1100.0, 165.0, 82.0, 32, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP);

-- Insert 200 novels distributed among the 20 authors
-- Create systematic distribution of novels among authors
WITH author_list AS (
    SELECT 
        u.uuid as author_id,
        u.username as author_name,
        ROW_NUMBER() OVER (ORDER BY u.uuid) as author_rank
    FROM users u
    WHERE u.is_author = true
    ORDER BY u.uuid
),
titles AS (
  -- novel_number, title
  VALUES
  (1,  'The Last Dragon'),
  (2,  'Shadows of the Past'),
  (3,  'Eternal Love'),
  (4,  'The Forbidden City'),
  (5,  'Blood Moon Rising'),
  (6,  'The Lost Princess'),
  (7,  'Thunder and Lightning'),
  (8,  'The Secret Garden'),
  (9,  'Warriors of Light'),
  (10, 'The Crimson Blade'),
  (11, 'Whispers in the Dark'),
  (12, 'The Golden Phoenix'),
  (13, 'Storm of Destiny'),
  (14, 'The Hidden Truth'),
  (15, 'Flames of Passion'),
  (16, 'The Silver Arrow'),
  (17, 'Echoes of Time'),
  (18, 'The Broken Crown'),
  (19, 'Winds of Change'),
  (20, 'The Final Quest'),
  (21, 'The Mystic Forest'),
  (22, 'Rising Phoenix'),
  (23, 'The Ancient Scroll'),
  (24, 'Moonlight Sonata'),
  (25, 'The Crystal Sword'),
  (26, 'Desert Winds'),
  (27, 'The Frozen Heart'),
  (28, 'Ocean Depths'),
  (29, 'The Burning Sky'),
  (30, 'Mountain Shadows'),
  (31, 'The Sacred Temple'),
  (32, 'River of Dreams'),
  (33, 'The Dark Knight'),
  (34, 'Starlight Journey'),
  (35, 'The Forgotten Realm'),
  (36, 'Fire and Ice'),
  (37, 'The Enchanted Rose'),
  (38, 'Thunderstorm'),
  (39, 'The Silent Guardian'),
  (40, 'Dawn of Heroes'),
  (41, 'The Lost Kingdom'),
  (42, 'Nightfall Chronicles'),
  (43, 'The Sacred Flame'),
  (44, 'Mystic Waters'),
  (45, 'The Eternal War'),
  (46, 'Shadow Walker'),
  (47, 'The Golden Age'),
  (48, 'Crimson Dawn'),
  (49, 'The Last Stand'),
  (50, 'Legends of Tomorrow'),

  -- 51–200: tên mới, đa dạng thể loại
  (51, 'City of Ash and Glass'),
  (52, 'Letters from a Distant Star'),
  (53, 'Bride of the Northern Sea'),
  (54, 'Clockwork Kingdom'),
  (55, 'A Deal with Starlight'),
  (56, 'Violet Crown'),
  (57, 'Harbor of Lost Ships'),
  (58, 'The Alchemist’s Heir'),
  (59, 'Lanterns at Dawn'),
  (60, 'Song of the Iron Wolf'),
  (61, 'Gilded Thorns'),
  (62, 'Archive of Broken Oaths'),
  (63, 'Queen of Embers'),
  (64, 'When Rivers Remember'),
  (65, 'The Seventh sigil'),
  (66, 'Cathedral of Snow'),
  (67, 'Paper Moons and Steel'),
  (68, 'The Sparrow and the Storm'),
  (69, 'Masks of the Jade Court'),
  (70, 'Nine Lives of Midnight'),
  (71, 'Oracle of Ruins'),
  (72, 'Beneath the Sapphire Tide'),
  (73, 'Hunter of Hollow Roads'),
  (74, 'Tea at the End of the World'),
  (75, 'Crownless'),
  (76, 'Grave of a Thousand Names'),
  (77, 'Embers on the Wind'),
  (78, 'The Marble Labyrinth'),
  (79, 'Silent Aria'),
  (80, 'Feather and Fang'),
  (81, 'Glass Gardens'),
  (82, 'The Courier and the King'),
  (83, 'Daughter of the Tide'),
  (84, 'Larkspur Pact'),
  (85, 'Anatomy of a Miracle'),
  (86, 'The Blackwater Bargain'),
  (87, 'Candles in the Rain'),
  (88, 'Ashes of the Orchard'),
  (89, 'Pilgrim of Two Suns'),
  (90, 'Thief of Winter'),
  (91, 'Juniper Road'),
  (92, 'The Prism War'),
  (93, 'Bridge of Silver Bones'),
  (94, 'The Cartographer’s Lies'),
  (95, 'Harbinger of Petals'),
  (96, 'A Crown of Quiet Fury'),
  (97, 'The Weathermancer'),
  (98, 'Kingdom of Paper Walls'),
  (99, 'The Violet Horizon'),
  (100,'Residual Magic'),

  (101,'The Clockmaker’s Daughter'),
  (102,'Bloom Where Night Falls'),
  (103,'A Prince of Dust'),
  (104,'Salt for the Serpent'),
  (105,'Courtyard of Echoes'),
  (106,'The Lantern Keepers'),
  (107,'Starfall Treaty'),
  (108,'An Heir of Ruined Maps'),
  (109,'Mercy of the Iron Sun'),
  (110,'The Painter of Storms'),
  (111,'Foxfire Oath'),
  (112,'Harps of the Red Vale'),
  (113,'The Blind Navigator'),
  (114,'Moon over Blackstone'),
  (115,'A Study in Wildfire'),
  (116,'The Widow’s Atlas'),
  (117,'Garden of Rust'),
  (118,'The Pearl Hypothesis'),
  (119,'Citadel of Paper Crowns'),
  (120,'A Wolf for Winter'),

  (121,'Tidesong Republic'),
  (122,'The Seamstress of Bones'),
  (123,'Amber and Ice'),
  (124,'The Lighthouse Paradox'),
  (125,'Bargain of the Blue Market'),
  (126,'Rain on the Violet Field'),
  (127,'The Sparrow King’s Debt'),
  (128,'Cathedral of Mists'),
  (129,'The Saffron Empire'),
  (130,'Whale Road'),
  (131,'Night Market Brides'),
  (132,'An Almanac of Ghosts'),
  (133,'The Hollow Orchard'),
  (134,'The Porcelain City'),
  (135,'Letters to the River'),
  (136,'The Last Mapmaker'),
  (137,'Gears of Heaven'),
  (138,'Bride of the Ember Court'),
  (139,'Onyx Orchard'),
  (140,'The Stormreader'),

  (141,'A Choir of Broken Swords'),
  (142,'The Jasmine War'),
  (143,'Emissary of Glass'),
  (144,'Mirrors for the Moon'),
  (145,'The Copper Prophet'),
  (146,'How to Catch a Thunderbird'),
  (147,'The Orchard at Night'),
  (148,'The Seventh Lighthouse'),
  (149,'Tamer of the Salt Wind'),
  (150,'A Crown of Smoke'),

  (151,'The Chronicle of Small Gods'),
  (152,'The Winter Archivist'),
  (153,'Pilgrim’s Lantern'),
  (154,'Gilded Winter'),
  (155,'The Tiger and the Tide'),
  (156,'Sleepers of the Sapphire Gate'),
  (157,'The Bellmaker’s Son'),
  (158,'A Garden for Ghosts'),
  (159,'Rook and Rose'),
  (160,'The Skyship Letters'),
  (161,'The Orchardist’s War'),
  (162,'Ink over Iron'),
  (163,'The Red Magnolia'),
  (164,'Voyage to No Maps'),
  (165,'The River Knows'),
  (166,'A Library of Salt'),
  (167,'The Snowbound Crown'),
  (168,'Petals for the Dead King'),
  (169,'The Seam of the World'),
  (170,'Queen of Low Tides'),

  (171,'The Ashen Spire'),
  (172,'Thunder in a Teacup'),
  (173,'The Lantern and the Wolf'),
  (174,'Letters from Sky Harbor'),
  (175,'The Cartographer’s Bride'),
  (176,'A Study in Starbones'),
  (177,'The Blue Sparrow Pact'),
  (178,'Amber Lanterns'),
  (179,'Cinder and Silk'),
  (180,'The Jade Astrolabe'),
  (181,'Seraphim Street'),
  (182,'Beneath the Iron Orchard'),
  (183,'The Salt Prince'),
  (184,'A Colder Kind of Fire'),
  (185,'The Paper Lion'),
  (186,'Harbor of Brass'),
  (187,'The Ninth Door'),
  (188,'Daughter of the Stormglass'),
  (189,'The Whale and the Willow'),
  (190,'King of Quiet Seas'),

  (191,'The Hourglass Embassy'),
  (192,'Foxes of the Winter Court'),
  (193,'The Violet Cartographer'),
  (194,'A Hundred Lanterns'),
  (195,'The Orchard of Suns'),
  (196,'Bride of the North Wind'),
  (197,'The Copper Crown Conspiracy'),
  (198,'Ashes on the Tidemap'),
  (199,'The Last Starlight Inn'),
  (200,'Cathedral of Falling Snow')
),
novel_assignments AS (
    SELECT 
        al.author_id,
        al.author_name,
        gs.novel_number
    FROM author_list al
    CROSS JOIN generate_series(1, 200) as gs(novel_number)
    WHERE (gs.novel_number - 1) % (SELECT COUNT(*) FROM author_list) = (al.author_rank - 1)
    ORDER BY gs.novel_number
)
INSERT INTO novel (
    uuid, title, author_id, author_name, category_id, synopsis, cover_img_url,
    status, is_completed, is_valid, chapter_cnt, word_cnt, avg_rating, review_cnt,
    view_cnt, vote_cnt, yuan_cnt, create_time, update_time, publish_time
)
SELECT 
    gen_random_uuid(),
    (SELECT t.column2 FROM titles t WHERE t.column1 = na.novel_number),
    na.author_id,
    na.author_name,
    FLOOR(random() * 15) + 1,
    CASE 
        WHEN na.novel_number % 50 = 1 THEN 'A legendary tale of the last dragon and its guardian, filled with magic and adventure.'
        WHEN na.novel_number % 50 = 2 THEN 'A haunting story of secrets buried in the past, waiting to be uncovered.'
        WHEN na.novel_number % 50 = 3 THEN 'A timeless romance that transcends time and space, touching hearts across generations.'
        WHEN na.novel_number % 50 = 4 THEN 'An epic journey through the forbidden city, where danger lurks at every corner.'
        WHEN na.novel_number % 50 = 5 THEN 'A supernatural thriller set during the blood moon, when ancient powers awaken.'
        WHEN na.novel_number % 50 = 6 THEN 'The story of a princess who disappeared, and the quest to find her.'
        WHEN na.novel_number % 50 = 7 THEN 'An action-packed adventure of two warriors bound by destiny and lightning.'
        WHEN na.novel_number % 50 = 8 THEN 'A mysterious garden holds secrets that could change the world forever.'
        WHEN na.novel_number % 50 = 9 THEN 'The battle between light and darkness, where heroes must rise.'
        WHEN na.novel_number % 50 = 10 THEN 'A legendary sword with the power to determine the fate of kingdoms.'
        WHEN na.novel_number % 50 = 11 THEN 'Dark whispers reveal a conspiracy that threatens to destroy everything.'
        WHEN na.novel_number % 50 = 12 THEN 'The golden phoenix rises from ashes, bringing hope to a dying world.'
        WHEN na.novel_number % 50 = 13 THEN 'A storm of destiny sweeps across the land, changing lives forever.'
        WHEN na.novel_number % 50 = 14 THEN 'The truth lies hidden, but some secrets are too dangerous to keep.'
        WHEN na.novel_number % 50 = 15 THEN 'A passionate love story that burns brighter than any flame.'
        WHEN na.novel_number % 50 = 16 THEN 'The silver arrow never misses its target, but this time it aims for the heart.'
        WHEN na.novel_number % 50 = 17 THEN 'Echoes from the past resurface, revealing a truth that changes everything.'
        WHEN na.novel_number % 50 = 18 THEN 'A broken crown, a divided kingdom, and the quest to restore peace.'
        WHEN na.novel_number % 50 = 19 THEN 'Winds of change sweep across the land, bringing new hope and new dangers.'
        WHEN na.novel_number % 50 = 20 THEN 'The final quest that will determine the fate of all worlds.'
        WHEN na.novel_number % 50 = 21 THEN 'Deep in the mystic forest, ancient magic awakens and calls to those brave enough to answer.'
        WHEN na.novel_number % 50 = 22 THEN 'From the ashes of destruction, a phoenix rises to bring hope to a dying world.'
        WHEN na.novel_number % 50 = 23 THEN 'An ancient scroll holds the key to unlocking powers that could save or destroy everything.'
        WHEN na.novel_number % 50 = 24 THEN 'Under the moonlight, a beautiful sonata plays, but its melody carries a deadly secret.'
        WHEN na.novel_number % 50 = 25 THEN 'The crystal sword glows with inner light, its power sought by heroes and villains alike.'
        WHEN na.novel_number % 50 = 26 THEN 'Across the endless desert, winds carry whispers of a lost civilization and its treasures.'
        WHEN na.novel_number % 50 = 27 THEN 'In the frozen north, a heart of ice begins to thaw, but love comes with a terrible price.'
        WHEN na.novel_number % 50 = 28 THEN 'Beneath the ocean depths, ancient cities sleep, waiting for the chosen one to awaken them.'
        WHEN na.novel_number % 50 = 29 THEN 'The sky burns with fire and fury as two ancient forces clash in an epic battle.'
        WHEN na.novel_number % 50 = 30 THEN 'High in the mountains, shadows move with purpose, and none who enter return unchanged.'
        WHEN na.novel_number % 50 = 31 THEN 'Within the sacred temple, ancient rituals are performed, but some secrets should remain buried.'
        WHEN na.novel_number % 50 = 32 THEN 'Along the river of dreams, reality and fantasy blur, and the line between them disappears.'
        WHEN na.novel_number % 50 = 33 THEN 'The dark knight rides through the night, seeking justice but finding only more darkness.'
        WHEN na.novel_number % 50 = 34 THEN 'Under the starlight, a journey begins that will take the traveler beyond the edge of the known world.'
        WHEN na.novel_number % 50 = 35 THEN 'In the forgotten realm, time stands still, and those who enter may never find their way back.'
        WHEN na.novel_number % 50 = 36 THEN 'Where fire meets ice, a new power is born, but it comes with a terrible cost.'
        WHEN na.novel_number % 50 = 37 THEN 'The enchanted rose blooms once every hundred years, and its petals hold the key to immortality.'
        WHEN na.novel_number % 50 = 38 THEN 'As the thunderstorm rages, ancient spirits awaken, and the living must choose their side.'
        WHEN na.novel_number % 50 = 39 THEN 'The silent guardian watches over the realm, but even guardians can be corrupted by power.'
        WHEN na.novel_number % 50 = 40 THEN 'At the dawn of a new age, heroes rise from the ashes of the old world.'
        WHEN na.novel_number % 50 = 41 THEN 'The lost kingdom calls to those brave enough to seek it, but finding it is only the beginning.'
        WHEN na.novel_number % 50 = 42 THEN 'As night falls, the chronicles of the past come alive, and history repeats itself.'
        WHEN na.novel_number % 50 = 43 THEN 'The sacred flame burns eternal, but those who seek to control it risk everything.'
        WHEN na.novel_number % 50 = 44 THEN 'In the mystic waters, ancient creatures dwell, and their wisdom comes at a price.'
        WHEN na.novel_number % 50 = 45 THEN 'The eternal war rages on, and the fate of all worlds hangs in the balance.'
        WHEN na.novel_number % 50 = 46 THEN 'The shadow walker moves between worlds, but each step brings them closer to madness.'
        WHEN na.novel_number % 50 = 47 THEN 'In the golden age, heroes and legends were born, but all golden ages must come to an end.'
        WHEN na.novel_number % 50 = 48 THEN 'As crimson dawn breaks, the final battle begins, and only one side can emerge victorious.'
        WHEN na.novel_number % 50 = 49 THEN 'This is the last stand, where heroes and villains meet their final destiny.'
        ELSE 'The legends of tomorrow are written today, and the choices we make will echo through eternity.'
    END,
    CASE 
        WHEN na.novel_number % 50 = 1 THEN 'dragon_cover.png'
        WHEN na.novel_number % 50 = 2 THEN 'shadows_cover.png'
        WHEN na.novel_number % 50 = 3 THEN 'eternal_love_cover.png'
        WHEN na.novel_number % 50 = 4 THEN 'forbidden_city_cover.png'
        WHEN na.novel_number % 50 = 5 THEN 'blood_moon_cover.png'
        WHEN na.novel_number % 50 = 6 THEN 'lost_princess_cover.png'
        WHEN na.novel_number % 50 = 7 THEN 'thunder_lightning_cover.png'
        WHEN na.novel_number % 50 = 8 THEN 'secret_garden_cover.png'
        WHEN na.novel_number % 50 = 9 THEN 'warriors_light_cover.png'
        WHEN na.novel_number % 50 = 10 THEN 'crimson_blade_cover.png'
        WHEN na.novel_number % 50 = 11 THEN 'whispers_dark_cover.png'
        WHEN na.novel_number % 50 = 12 THEN 'golden_phoenix_cover.png'
        WHEN na.novel_number % 50 = 13 THEN 'storm_destiny_cover.png'
        WHEN na.novel_number % 50 = 14 THEN 'hidden_truth_cover.png'
        WHEN na.novel_number % 50 = 15 THEN 'flames_passion_cover.png'
        WHEN na.novel_number % 50 = 16 THEN 'silver_arrow_cover.png'
        WHEN na.novel_number % 50 = 17 THEN 'echoes_time_cover.png'
        WHEN na.novel_number % 50 = 18 THEN 'broken_crown_cover.png'
        WHEN na.novel_number % 50 = 19 THEN 'winds_change_cover.png'
        WHEN na.novel_number % 50 = 20 THEN 'final_quest_cover.png'
        WHEN na.novel_number % 50 = 21 THEN 'mystic_forest_cover.png'
        WHEN na.novel_number % 50 = 22 THEN 'rising_phoenix_cover.png'
        WHEN na.novel_number % 50 = 23 THEN 'ancient_scroll_cover.png'
        WHEN na.novel_number % 50 = 24 THEN 'moonlight_sonata_cover.png'
        WHEN na.novel_number % 50 = 25 THEN 'crystal_sword_cover.png'
        WHEN na.novel_number % 50 = 26 THEN 'desert_winds_cover.png'
        WHEN na.novel_number % 50 = 27 THEN 'frozen_heart_cover.png'
        WHEN na.novel_number % 50 = 28 THEN 'ocean_depths_cover.png'
        WHEN na.novel_number % 50 = 29 THEN 'burning_sky_cover.png'
        WHEN na.novel_number % 50 = 30 THEN 'mountain_shadows_cover.png'
        WHEN na.novel_number % 50 = 31 THEN 'sacred_temple_cover.png'
        WHEN na.novel_number % 50 = 32 THEN 'river_dreams_cover.png'
        WHEN na.novel_number % 50 = 33 THEN 'dark_knight_cover.png'
        WHEN na.novel_number % 50 = 34 THEN 'starlight_journey_cover.png'
        WHEN na.novel_number % 50 = 35 THEN 'forgotten_realm_cover.png'
        WHEN na.novel_number % 50 = 36 THEN 'fire_ice_cover.png'
        WHEN na.novel_number % 50 = 37 THEN 'enchanted_rose_cover.png'
        WHEN na.novel_number % 50 = 38 THEN 'thunderstorm_cover.png'
        WHEN na.novel_number % 50 = 39 THEN 'silent_guardian_cover.png'
        WHEN na.novel_number % 50 = 40 THEN 'dawn_heroes_cover.png'
        WHEN na.novel_number % 50 = 41 THEN 'lost_kingdom_cover.png'
        WHEN na.novel_number % 50 = 42 THEN 'nightfall_chronicles_cover.png'
        WHEN na.novel_number % 50 = 43 THEN 'sacred_flame_cover.png'
        WHEN na.novel_number % 50 = 44 THEN 'mystic_waters_cover.png'
        WHEN na.novel_number % 50 = 45 THEN 'eternal_war_cover.png'
        WHEN na.novel_number % 50 = 46 THEN 'shadow_walker_cover.png'
        WHEN na.novel_number % 50 = 47 THEN 'golden_age_cover.png'
        WHEN na.novel_number % 50 = 48 THEN 'crimson_dawn_cover.png'
        WHEN na.novel_number % 50 = 49 THEN 'last_stand_cover.png'
        ELSE 'legends_tomorrow_cover.png'
    END,
    FLOOR(random() * 3)::int, -- 0=DRAFT, 1=PUBLISHED, 2=ARCHIVED
    CASE WHEN random() < 0.1 THEN true ELSE false END, -- 10% completed
    true,
    FLOOR(random() * 4) + 2, -- 2-5 chapters
    FLOOR(random() * 100000) + 10000, -- 10k-110k words
    (random() * 2) + 3, -- 3.0-5.0 rating
    FLOOR(random() * 50), -- 0-49 reviews
    FLOOR(random() * 5000), -- 0-4999 views
    FLOOR(random() * 200), -- 0-199 votes
    (random() * 500)::numeric(10,2), -- 0-500 yuan
    CURRENT_TIMESTAMP - INTERVAL '60 days' + (random() * INTERVAL '60 days'),
    CURRENT_TIMESTAMP - INTERVAL '1 day',
    CASE 
        WHEN FLOOR(random() * 3) = 1 THEN 
            CURRENT_TIMESTAMP - INTERVAL '60 days' + (random() * INTERVAL '60 days')
        ELSE NULL 
    END
FROM novel_assignments na
ORDER BY na.novel_number;

-- Insert 500+ chapters distributed among novels
-- Create chapters systematically to avoid duplicate (novel_id, chapter_number)
INSERT INTO chapter (
    uuid, novel_id, chapter_number, title, content, word_cnt, is_premium,
    yuan_cost, view_cnt, is_valid, create_time, update_time, publish_time
)
SELECT 
    gen_random_uuid(),
    n.id as novel_id,
    gs.chapter_number,
    'Chapter ' || gs.chapter_number || ' of Novel ' || n.id,
    'This is the content of chapter ' || gs.chapter_number || ' of novel ' || n.id || '. ' ||
    'Lorem ipsum dolor sit amet, consectetur adipiscing elit. ' ||
    'Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.',
    FLOOR(random() * 3000) + 1000, -- 1000-4000 words
    CASE WHEN random() < 0.3 THEN true ELSE false END, -- 30% premium
    CASE WHEN random() < 0.3 THEN (random() * 10)::numeric(10,2) ELSE 0 END,
    FLOOR(random() * 1000), -- 0-999 views
    true,
    CURRENT_TIMESTAMP - INTERVAL '60 days' + (random() * INTERVAL '60 days'),
    CURRENT_TIMESTAMP - INTERVAL '1 day',
    CURRENT_TIMESTAMP - INTERVAL '60 days' + (random() * INTERVAL '60 days')
FROM novel n
CROSS JOIN generate_series(1, 5) as gs(chapter_number)
WHERE n.id <= 200
ORDER BY random()
LIMIT 500;

-- Insert 1000+ reviews from readers
-- Create unique (user_id, novel_id) combinations to avoid duplicate key errors
WITH review_combinations AS (
    SELECT 
        u.uuid as user_id,
        n.id as novel_id,
        ROW_NUMBER() OVER (ORDER BY random()) as rn
    FROM users u
    CROSS JOIN novel n
    WHERE u.is_author = false
    ORDER BY random()
    LIMIT 1000
)
INSERT INTO review (
    uuid, user_id, novel_id, rating, title, content, like_cnt, is_spoiler,
    create_time, update_time
)
SELECT 
    gen_random_uuid(),
    rc.user_id,
    rc.novel_id,
    FLOOR(random() * 5) + 1, -- Rating 1-5
    'Review for Novel ' || rc.novel_id,
    'This is a review for novel ' || rc.novel_id || '. ' ||
    'The story was engaging and well-written. I would recommend it to others.',
    FLOOR(random() * 20), -- 0-19 likes
    CASE WHEN random() < 0.1 THEN true ELSE false END, -- 10% spoilers
    CURRENT_TIMESTAMP - INTERVAL '60 days' + (random() * INTERVAL '60 days'),
    CURRENT_TIMESTAMP - INTERVAL '1 day'
FROM review_combinations rc;

-- Insert 2000+ comments from readers
INSERT INTO comment (
    user_id, chapter_id, content, like_cnt, is_spoiler, create_time, update_time
)
SELECT 
    (SELECT uuid FROM users WHERE is_author = false ORDER BY random() LIMIT 1),
    FLOOR(random() * 500) + 1, -- Random chapter_id 1-500
    'Comment ' || generate_series(1, 2000) || ': Great chapter!',
    FLOOR(random() * 10), -- 0-9 likes
    CASE WHEN random() < 0.05 THEN true ELSE false END, -- 5% spoilers
    CURRENT_TIMESTAMP - INTERVAL '60 days' + (random() * INTERVAL '60 days'),
    CURRENT_TIMESTAMP - INTERVAL '1 day';

-- Insert 2000+ votes from readers
-- Create unique (user_id, novel_id) combinations to avoid duplicate key errors
WITH vote_combinations AS (
    SELECT 
        u.uuid as user_id,
        n.id as novel_id,
        ROW_NUMBER() OVER (ORDER BY random()) as rn
    FROM users u
    CROSS JOIN novel n
    WHERE u.is_author = false
    ORDER BY random()
    LIMIT 2000
)
INSERT INTO vote (
    user_id, novel_id, create_time, update_time
)
SELECT 
    vc.user_id,
    vc.novel_id,
    CURRENT_TIMESTAMP - INTERVAL '60 days' + (random() * INTERVAL '60 days'),
    CURRENT_TIMESTAMP - INTERVAL '1 day'
FROM vote_combinations vc;

-- Insert 3000+ history records from readers
-- Create realistic reading history with proper novel-chapter relationships
WITH reading_history AS (
    SELECT 
        u.uuid as user_id,
        n.id as novel_id,
        ch.id as chapter_id,
        ROW_NUMBER() OVER (ORDER BY random()) as rn
    FROM users u
    CROSS JOIN novel n
    CROSS JOIN chapter ch
    WHERE u.is_author = false 
    AND ch.novel_id = n.id
    ORDER BY random()
    LIMIT 3000
)
INSERT INTO history (
    uuid, user_id, novel_id, chapter_id, create_time, update_time
)
SELECT 
    gen_random_uuid(),
    rh.user_id,
    rh.novel_id,
    rh.chapter_id,
    CURRENT_TIMESTAMP - INTERVAL '60 days' + (random() * INTERVAL '60 days'),
    CURRENT_TIMESTAMP - INTERVAL '1 day'
FROM reading_history rh;

-- Insert 800+ reports from readers (distributed among all readers)
INSERT INTO report (
    uuid, reporter_id, report_type, reason, status, admin_notes, resolved_by,
    content_type, content_id, created_at, updated_at
)
WITH report_combinations AS (
    SELECT 
        u.uuid as reporter_id,
        ROW_NUMBER() OVER (ORDER BY random()) as rn
    FROM users u
    WHERE u.is_author = false
    ORDER BY random()
    LIMIT 800
)
SELECT 
    gen_random_uuid(),
    rc.reporter_id,
    (ARRAY['INAPPROPRIATE', 'SPAM', 'HATE_BULLYING', 'PORNOGRAPHIC', 'PERSONAL_INFO'])[FLOOR(random() * 5) + 1]::report_type,
    'Report reason ' || rc.rn,
    (ARRAY['IN_REVIEW', 'RESOLVED', 'DISMISSED'])[FLOOR(random() * 3) + 1]::report_status,
    CASE WHEN random() < 0.5 THEN 'Admin notes for report ' || rc.rn ELSE NULL END,
    CASE WHEN random() < 0.3 THEN (SELECT uuid FROM users WHERE is_admin = true LIMIT 1) ELSE NULL END,
    (ARRAY['NOVEL', 'COMMENT'])[FLOOR(random() * 2) + 1],
    FLOOR(random() * 200) + 1, -- Random content_id 1-200
    CURRENT_TIMESTAMP - INTERVAL '60 days' + (random() * INTERVAL '60 days'),
    CURRENT_TIMESTAMP - INTERVAL '1 day'
FROM report_combinations rc;