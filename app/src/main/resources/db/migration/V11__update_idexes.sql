-- Modify table to use generated columns
ALTER TABLE items
  ADD COLUMN year  integer GENERATED ALWAYS AS (EXTRACT(YEAR FROM timezone('UTC', time))) STORED,
  ADD COLUMN month integer GENERATED ALWAYS AS (EXTRACT(MONTH FROM timezone('UTC', time))) STORED;

-- Create indexes
CREATE INDEX idx_items_score_desc
  ON items (score DESC, descendants DESC)
  WHERE score > 1000 AND type NOT IN ('comment', 'pollopt');

CREATE INDEX idx_items_descendants_desc
  ON items (descendants DESC, score DESC)
  WHERE descendants > 700 AND type NOT IN ('comment', 'pollopt');

CREATE INDEX idx_items_year_month_by_score ON items (year, month, score DESC, descendants DESC) WHERE type NOT IN ('comment', 'pollopt');
CREATE INDEX idx_items_year_month_by_descendants ON items (year, month, descendants DESC, score DESC) WHERE type NOT IN ('comment', 'pollopt');
