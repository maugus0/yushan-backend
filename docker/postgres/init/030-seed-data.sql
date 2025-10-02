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
('Inactive One', 'Testing Ahan Jaiswal', 'inactive-one', true)
('Inactive Two', 'Testing Ahan Jaiswal', 'inactive-two', true)
ON CONFLICT (slug) DO NOTHING;