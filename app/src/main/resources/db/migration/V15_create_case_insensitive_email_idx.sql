-- Drop existing index
DROP INDEX subscribers_email_key;

-- Create new case-insensitive unique index using PostgreSQL specific LOWER function
CREATE UNIQUE INDEX subscribers_email_key ON subscribers (LOWER(email));
