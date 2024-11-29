-- V2__rename_subscriber_table_and_column.sql
BEGIN;

ALTER TABLE subscription
    DROP CONSTRAINT fk_subscriber;

-- Drop primary key and unique constraints first
ALTER TABLE subscriber
    DROP CONSTRAINT subscriber_pkey,
    DROP CONSTRAINT subscriber_email_key;

-- Rename table
ALTER TABLE subscriber
    RENAME TO subscribers;

-- Rename column
ALTER TABLE subscribers
    RENAME COLUMN subsuuid TO subscriber_id;

-- Add constraints back on renamed table
ALTER TABLE subscribers
    ADD CONSTRAINT subscribers_pkey PRIMARY KEY (id),
    ADD CONSTRAINT subscribers_email_key UNIQUE (email);

-- Re-add the foreign key constraint with new table name
ALTER TABLE subscription
    ADD CONSTRAINT fk_subscriber
    FOREIGN KEY (subscriber_id)
    REFERENCES subscribers(id);

COMMIT;
