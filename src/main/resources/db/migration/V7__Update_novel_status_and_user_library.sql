-- V7: Update novel status and user library data
-- This migration updates novel statuses and creates realistic library data

-- 1. Set all completed novels to PUBLISHED status (status = 2)
UPDATE novel 
SET status = 2 
WHERE is_completed = true;

-- 2. Update user status distribution
-- Set 50 users to status 0, rest random between 1 and 2
WITH user_updates AS (
    SELECT 
        uuid,
        ROW_NUMBER() OVER (ORDER BY create_time) as rn
    FROM users
    WHERE is_admin = false
)
UPDATE users 
SET status = CASE 
    WHEN uu.rn <= 50 THEN 0
    ELSE FLOOR(random() * 2) + 1
END
FROM user_updates uu
WHERE users.uuid = uu.uuid;

-- 3. Create library for each user
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

-- 4. Add realistic novel_library data
-- Each user will have 3-15 novels in their library with realistic progress
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
    -- Realistic progress distribution:
    -- 20% completed (100), 30% in progress (1-99), 50% not started (0)
    CASE 
        WHEN random() < 0.2 THEN 100
        WHEN random() < 0.5 THEN FLOOR(random() * 99) + 1
        ELSE 0
    END as progress,
    CURRENT_TIMESTAMP - INTERVAL '60 days' + (random() * INTERVAL '60 days'),
    CURRENT_TIMESTAMP
FROM filtered_assignments fa
WHERE NOT EXISTS (
    SELECT 1 FROM novel_library nl 
    WHERE nl.library_id = fa.library_id 
    AND nl.novel_id = fa.novel_id
);

-- 5. Add some additional realistic library entries for popular novels
-- This ensures popular novels appear in more libraries
WITH popular_novels AS (
    SELECT 
        n.id as novel_id,
        n.view_cnt,
        ROW_NUMBER() OVER (ORDER BY n.view_cnt DESC) as popularity_rank
    FROM novel n
    WHERE n.status = 2 AND n.is_valid = true
    ORDER BY n.view_cnt DESC
    LIMIT 50 -- Top 50 most viewed novels
),
additional_assignments AS (
    SELECT 
        l.id as library_id,
        l.user_id,
        pn.novel_id,
        ROW_NUMBER() OVER (PARTITION BY l.user_id ORDER BY random()) as rn
    FROM library l
    CROSS JOIN popular_novels pn
    WHERE pn.popularity_rank <= 20 -- Top 20 novels
    ORDER BY l.user_id, random()
),
filtered_additional AS (
    SELECT 
        library_id,
        user_id,
        novel_id,
        rn
    FROM additional_assignments
    WHERE rn <= 3 -- Add up to 3 more popular novels per user
)
INSERT INTO novel_library (library_id, novel_id, progress, create_time, update_time)
SELECT 
    fa.library_id,
    fa.novel_id,
    -- Higher completion rate for popular novels
    CASE 
        WHEN random() < 0.4 THEN 100 -- 40% completed
        WHEN random() < 0.7 THEN FLOOR(random() * 99) + 1 -- 30% in progress
        ELSE 0 -- 30% not started
    END as progress,
    CURRENT_TIMESTAMP - INTERVAL '45 days' + (random() * INTERVAL '45 days'),
    CURRENT_TIMESTAMP
FROM filtered_additional fa
WHERE NOT EXISTS (
    SELECT 1 FROM novel_library nl 
    WHERE nl.library_id = fa.library_id 
    AND nl.novel_id = fa.novel_id
);

-- 6. Update some library entries to have more realistic reading patterns
-- Some users should have more completed novels, others should have more in-progress
UPDATE novel_library 
SET progress = CASE 
    WHEN random() < 0.3 THEN 100 -- 30% chance to be completed
    WHEN random() < 0.6 THEN FLOOR(random() * 50) + 50 -- 30% chance to be 50-99% complete
    ELSE FLOOR(random() * 49) + 1 -- 40% chance to be 1-49% complete
END,
update_time = CURRENT_TIMESTAMP
WHERE random() < 0.4; -- Update 40% of existing entries

-- 7. Add some library entries for recently published novels
-- Newer novels should appear in more libraries
WITH recent_novels AS (
    SELECT 
        n.id as novel_id,
        n.create_time
    FROM novel n
    WHERE n.status = 2 
    AND n.is_valid = true
    AND n.create_time > CURRENT_TIMESTAMP - INTERVAL '30 days'
),
recent_assignments AS (
    SELECT 
        l.id as library_id,
        l.user_id,
        rn.novel_id,
        ROW_NUMBER() OVER (PARTITION BY l.user_id ORDER BY random()) as rn
    FROM library l
    CROSS JOIN recent_novels rn
    ORDER BY l.user_id, random()
),
filtered_recent AS (
    SELECT 
        library_id,
        user_id,
        novel_id,
        rn
    FROM recent_assignments
    WHERE rn <= 2 -- Add up to 2 recent novels per user
)
INSERT INTO novel_library (library_id, novel_id, progress, create_time, update_time)
SELECT 
    fr.library_id,
    fr.novel_id,
    -- Recent novels are more likely to be in progress or not started
    CASE 
        WHEN random() < 0.1 THEN 100 -- 10% completed
        WHEN random() < 0.4 THEN FLOOR(random() * 30) + 1 -- 30% in early progress
        ELSE 0 -- 60% not started
    END as progress,
    CURRENT_TIMESTAMP - INTERVAL '15 days' + (random() * INTERVAL '15 days'),
    CURRENT_TIMESTAMP
FROM filtered_recent fr
WHERE NOT EXISTS (
    SELECT 1 FROM novel_library nl 
    WHERE nl.library_id = fr.library_id 
    AND nl.novel_id = fr.novel_id
);
