-- Indexes with score/descendants leading allow PostgreSQL to scan from the top
-- of the sorted order, filter by time, and stop at LIMIT 30 without sorting.
-- This replaces the previous approach of scanning the full time range and sorting,
-- which was slow for the rolling-365-day "This Year" query.
CREATE INDEX idx_items_score_time_desc
  ON items (score DESC, descendants DESC, time DESC)
  WHERE type NOT IN ('comment', 'pollopt');

CREATE INDEX idx_items_descendants_time_desc
  ON items (descendants DESC, score DESC, time DESC)
  WHERE type NOT IN ('comment', 'pollopt');
