CREATE INDEX idx_items_year_by_score ON items (year, score DESC, descendants DESC) WHERE type NOT IN ('comment', 'pollopt');
CREATE INDEX idx_items_year_by_descendants ON items (year, descendants DESC, score DESC) WHERE type NOT IN ('comment', 'pollopt');
