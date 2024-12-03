-- Modify table to use generated columns
ALTER TABLE items
ADD COLUMN year integer GENERATED ALWAYS AS (EXTRACT(YEAR FROM time)) STORED,
ADD COLUMN month integer GENERATED ALWAYS AS (EXTRACT(MONTH FROM time)) STORED;

-- Create indexes
CREATE INDEX idx_items_score_desc ON items (score DESC) where score > 1000;
CREATE INDEX idx_items_descendants_desc ON items (descendants DESC) WHERE descendants > 700;
CREATE INDEX idx_items_year_month ON items (year, month);
