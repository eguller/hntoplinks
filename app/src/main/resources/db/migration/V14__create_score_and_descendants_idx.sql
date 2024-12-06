DROP INDEX items_time_type_score_descendants_idx;

CREATE INDEX items_time_score_descendants_idx ON items (time DESC, score DESC, descendants DESC) WHERE type NOT IN ('comment', 'pollopt');
CREATE INDEX items_time_descendants_score_idx ON items (time DESC, descendants DESC, score DESC) WHERE type NOT IN ('comment', 'pollopt');
