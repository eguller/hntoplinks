CREATE INDEX items_time_type_score_descendants_idx ON items (time DESC, type, score DESC, descendants DESC);
