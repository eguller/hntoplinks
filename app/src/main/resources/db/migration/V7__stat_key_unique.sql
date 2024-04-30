ALTER table statistic DROP CONSTRAINT IF EXISTS stat_key_unique;
ALTER table statistic ADD CONSTRAINT stat_key_unique UNIQUE (stat_key);
