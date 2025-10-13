-- V10: Fix chapter numbers and titles
-- This migration fixes chapter numbering and updates titles with realistic names
-- WITHOUT deleting existing data, only updating chapter_number and title

-- 1. Fix chapter numbers to be sequential within each novel
-- First, set all chapter numbers to negative values to avoid unique constraint conflicts
UPDATE chapter 
SET chapter_number = -chapter_number
WHERE is_valid = true;

-- Then, update with correct sequential numbers
WITH chapter_renumbering AS (
    SELECT 
        c.id,
        c.novel_id,
        ROW_NUMBER() OVER (PARTITION BY c.novel_id ORDER BY c.id) as new_chapter_number
    FROM chapter c
    WHERE c.is_valid = true
)
UPDATE chapter 
SET chapter_number = crn.new_chapter_number
FROM chapter_renumbering crn
WHERE chapter.id = crn.id;

-- 2. Update chapter titles with realistic names
-- Create a list of 20 realistic chapter titles
WITH chapter_titles AS (
    SELECT unnest(ARRAY[
        'The Beginning of the Journey',
        'A Fateful Encounter',
        'Secrets Revealed',
        'The First Challenge',
        'An Unexpected Alliance',
        'The Decisive Battle',
        'A Moment of Weakness',
        'Hidden Power',
        'Broken Promises',
        'New Hope',
        'The Final Confrontation',
        'Victory and Defeat',
        'Deep Secrets',
        'Love and Hatred',
        'The Difficult Choice',
        'An Unexpected Turn',
        'The Quest Begins',
        'Truth Unveiled',
        'United Against Fate',
        'Endings and New Beginnings'
    ]) as title
),
numbered_titles AS (
    SELECT 
        title,
        ROW_NUMBER() OVER (ORDER BY title) as title_number
    FROM chapter_titles
)
UPDATE chapter 
SET title = nt.title
FROM numbered_titles nt
WHERE chapter.chapter_number = nt.title_number
AND chapter.is_valid = true;

-- 3. For chapters beyond 20, use generic titles with chapter numbers
UPDATE chapter 
SET title = 'Chapter ' || chapter_number
WHERE chapter_number > 20
AND is_valid = true;

-- 4. Update novel chapter counts to ensure consistency
UPDATE novel 
SET chapter_cnt = (
    SELECT COUNT(*) 
    FROM chapter c 
    WHERE c.novel_id = novel.id 
    AND c.is_valid = true
)
WHERE is_valid = true;

-- 5. Update novel word counts to match actual chapters
UPDATE novel 
SET word_cnt = (
    SELECT COALESCE(SUM(c.word_cnt), 0)
    FROM chapter c 
    WHERE c.novel_id = novel.id 
    AND c.is_valid = true
)
WHERE is_valid = true;
