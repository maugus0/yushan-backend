-- V9: Fix all data inconsistencies in seed data
-- This migration fixes multiple issues found in V6 seed data WITHOUT deleting existing data

-- 1. No need to add chapters - keep existing chapters as they are
-- Only update chapter counts to match actual chapters

-- 2. Fix novel chapter counts to match actual chapters
UPDATE novel 
SET chapter_cnt = (
    SELECT COUNT(*) 
    FROM chapter c 
    WHERE c.novel_id = novel.id 
    AND c.is_valid = true
)
WHERE is_valid = true;

-- 3. Fix novel word counts to match actual chapters
UPDATE novel 
SET word_cnt = (
    SELECT COALESCE(SUM(c.word_cnt), 0)
    FROM chapter c 
    WHERE c.novel_id = novel.id 
    AND c.is_valid = true
)
WHERE is_valid = true;

-- 4. Fix novel status and completion status based on chapter count
-- Novels with 0 chapters must be DRAFT and not completed
UPDATE novel 
SET status = 0, -- DRAFT
    is_completed = false
WHERE is_valid = true 
AND chapter_cnt = 0;

-- 5. Fix review data inconsistencies
-- Update reviews to reference valid novels only
UPDATE review 
SET novel_id = (
    SELECT MIN(n.id) 
    FROM novel n 
    WHERE n.is_valid = true 
    ORDER BY random() 
    LIMIT 1
)
WHERE novel_id NOT IN (
    SELECT id FROM novel WHERE is_valid = true
);

-- 6. Fix comment data inconsistencies  
-- Update comments to reference valid chapters only
UPDATE comment 
SET chapter_id = (
    SELECT MIN(c.id) 
    FROM chapter c 
    WHERE c.is_valid = true 
    ORDER BY random() 
    LIMIT 1
)
WHERE chapter_id NOT IN (
    SELECT id FROM chapter WHERE is_valid = true
);

-- 7. Fix vote data inconsistencies
-- Update votes to reference valid novels only
UPDATE vote 
SET novel_id = (
    SELECT MIN(n.id) 
    FROM novel n 
    WHERE n.is_valid = true 
    ORDER BY random() 
    LIMIT 1
)
WHERE novel_id NOT IN (
    SELECT id FROM novel WHERE is_valid = true
);

-- 8. Fix history data inconsistencies
-- Update history records to reference valid novels/chapters only
UPDATE history 
SET novel_id = (
    SELECT MIN(n.id) 
    FROM novel n 
    WHERE n.is_valid = true 
    ORDER BY random() 
    LIMIT 1
),
chapter_id = (
    SELECT MIN(c.id) 
    FROM chapter c 
    WHERE c.is_valid = true 
    ORDER BY random() 
    LIMIT 1
)
WHERE novel_id NOT IN (
    SELECT id FROM novel WHERE is_valid = true
)
OR chapter_id NOT IN (
    SELECT id FROM chapter WHERE is_valid = true
);

-- 9. Fix report data inconsistencies
-- Update report content_id to valid novel IDs only
UPDATE report 
SET content_id = (
    SELECT MIN(n.id) 
    FROM novel n 
    WHERE n.is_valid = true 
    ORDER BY random() 
    LIMIT 1
)
WHERE content_type = 'NOVEL' 
AND content_id NOT IN (
    SELECT id FROM novel WHERE is_valid = true
);

-- 10. Fix novel_library data inconsistencies
-- Update library entries to reference valid novels only
UPDATE novel_library 
SET novel_id = (
    SELECT MIN(n.id) 
    FROM novel n 
    WHERE n.is_valid = true 
    ORDER BY random() 
    LIMIT 1
)
WHERE novel_id NOT IN (
    SELECT id FROM novel WHERE is_valid = true
);

-- 11. Update novel statistics to be consistent
-- Update review counts
UPDATE novel 
SET review_cnt = (
    SELECT COUNT(*) 
    FROM review r 
    WHERE r.novel_id = novel.id
)
WHERE is_valid = true;

-- Update vote counts
UPDATE novel 
SET vote_cnt = (
    SELECT COUNT(*) 
    FROM vote v 
    WHERE v.novel_id = novel.id
)
WHERE is_valid = true;

-- Update average ratings
UPDATE novel 
SET avg_rating = (
    SELECT COALESCE(AVG(r.rating), 0.0)
    FROM review r 
    WHERE r.novel_id = novel.id
)
WHERE is_valid = true;

-- 11. Fix user library data
-- Ensure each user has a library
INSERT INTO library (uuid, user_id, create_time, update_time)
SELECT 
    gen_random_uuid(),
    u.uuid,
    u.create_time + INTERVAL '1 day',
    CURRENT_TIMESTAMP
FROM users u
WHERE NOT EXISTS (
    SELECT 1 FROM library l WHERE l.user_id = u.uuid
);

-- 12. Add realistic novel_library data for all users
-- Each user gets 3-15 novels in their library
WITH user_library_assignments AS (
    SELECT 
        l.id as library_id,
        l.user_id,
        n.id as novel_id,
        ROW_NUMBER() OVER (PARTITION BY l.user_id ORDER BY random()) as rn
    FROM library l
    CROSS JOIN novel n
    WHERE n.status = 2 -- Only published novels
    AND n.is_valid = true
    ORDER BY l.user_id, random()
),
filtered_assignments AS (
    SELECT 
        library_id,
        user_id,
        novel_id,
        rn
    FROM user_library_assignments
    WHERE rn <= (FLOOR(random() * 13) + 3) -- 3-15 novels per user
)
INSERT INTO novel_library (library_id, novel_id, progress, create_time, update_time)
SELECT 
    fa.library_id,
    fa.novel_id,
    FLOOR(random() * 5) + 1, -- Progress 1-5 chapters
    CURRENT_TIMESTAMP - INTERVAL '30 days' + (random() * INTERVAL '30 days'),
    CURRENT_TIMESTAMP
FROM filtered_assignments fa
WHERE NOT EXISTS (
    SELECT 1 FROM novel_library nl 
    WHERE nl.library_id = fa.library_id 
    AND nl.novel_id = fa.novel_id
);
