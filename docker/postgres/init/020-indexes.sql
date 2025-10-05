-- =====================================================
-- 020-indexes.sql - Essential Indexes for Performance
-- =====================================================

-- Essential Indexes for Novel Pagination and Filtering
-- These indexes are optimized for the most common query patterns

-- Primary filtering index (most important)
CREATE INDEX IF NOT EXISTS idx_novel_valid_category_status 
ON novel (is_valid, category_id, status) 
WHERE is_valid = true;

-- Default sorting index (create_time DESC is the default)
CREATE INDEX IF NOT EXISTS idx_novel_create_time_desc 
ON novel (create_time DESC) 
WHERE is_valid = true;

-- Author filtering index
CREATE INDEX IF NOT EXISTS idx_novel_valid_author 
ON novel (is_valid, author_id) 
WHERE is_valid = true;
