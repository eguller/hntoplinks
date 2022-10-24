ALTER TABLE subscription RENAME TO tmp_subscription;

CREATE TABLE IF NOT EXISTS subscriber
(
  id                bigint                      NOT NULL,
  activated         boolean,
  activation_date   timestamp without time zone,
  email             character varying(255)      NOT NULL,
  subsuuid          character varying(255)      NOT NULL,
  subscription_date timestamp without time zone NOT NULL,
  timezone          character varying(255)      NOT NULL,
  CONSTRAINT subscriber_pkey PRIMARY KEY (id),
  CONSTRAINT subscriber_email_key UNIQUE (email)
);

insert into subscriber select id, activated, activation_date, email, subsuuid, subscription_date, timezone from tmp_subscription;

CREATE TYPE period AS ENUM('DAILY', 'WEEKLY', 'MONTHLY', 'YEARLY');

CREATE TABLE IF NOT EXISTS subscription
(
  id               bigint      NOT NULL,
  period           period      NOT NULL,
  subscriber_id    bigint      NOT NULL,
  CONSTRAINT subscription_pkey PRIMARY KEY (id),
  CONSTRAINT fk_subscriber
      FOREIGN KEY(subscriber_id)
	      REFERENCES subscriber(id)
);

insert into subscription select nextval('hibernate_sequence'), period.DAILY, id from tmp_subscriber where daily = 'true';
insert into subscription select nextval('hibernate_sequence'), period.WEEKLY, id from tmp_subscriber where weekly = 'true';
insert into subscription select nextval('hibernate_sequence'), period.MONTHLY, id from tmp_subscriber where monthly = 'true';
insert into subscription select nextval('hibernate_sequence'), period.YEARLY, id from tmp_subscriber where annually = 'true';

drop table tmp_subscription;



