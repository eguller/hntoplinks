BEGIN;

-- Drop primary key and unique constraints first
ALTER TABLE subscription
    DROP CONSTRAINT subscription_pkey;

-- Rename table
ALTER TABLE subscription
    RENAME TO subscriptions;

-- Add constraints back with new table name
ALTER TABLE subscriptions
    ADD CONSTRAINT subscriptions_pkey PRIMARY KEY (id);

COMMIT;
