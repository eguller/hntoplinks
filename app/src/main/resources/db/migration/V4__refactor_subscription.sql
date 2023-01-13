ALTER TABLE subscription RENAME TO tmp_subscription;
ALTER TABLE tmp_subscription DROP CONSTRAINT subscription_pkey;

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

CREATE TABLE IF NOT EXISTS subscription
(
  id                 bigint      NOT NULL,
  period             character varying(255)      NOT NULL,
  subscriber_id      bigint      NOT NULL,
  next_send_date     timestamp without time zone NOT NULL,
  CONSTRAINT subscription_pkey PRIMARY KEY (id),
  CONSTRAINT fk_subscriber
      FOREIGN KEY(subscriber_id)
	      REFERENCES subscriber(id)
);

insert into subscription select nextval('hibernate_sequence'), 'DAILY', id, next_send_day from tmp_subscription where daily = 'true';
insert into subscription select nextval('hibernate_sequence'), 'WEEKLY', id, next_send_week from tmp_subscription where weekly = 'true';
insert into subscription select nextval('hibernate_sequence'), 'MONTHLY', id, next_send_month from tmp_subscription where monthly = 'true';
insert into subscription select nextval('hibernate_sequence'), 'YEARLY', id, next_send_year from tmp_subscription where annually = 'true';

drop table tmp_subscription;



