DROP TABLE IF EXISTS vote;
CREATE TABLE vote (
    id SERIAL PRIMARY KEY,
    user_id UUID NOT NULL,
    novel_id INTEGER NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
-- Insert 20 votes (users voting on novels)
INSERT INTO vote (user_id, novel_id, create_time, update_time)
SELECT
    u.uuid,
    n.id,
    CURRENT_TIMESTAMP - INTERVAL '25 days',
    CURRENT_TIMESTAMP - INTERVAL '1 day'
FROM users u
    CROSS JOIN novel n
WHERE u.email LIKE 'reader%' OR u.email = 'user@yushan.com'
  AND n.id <= 20
    LIMIT 20;