CREATE SEQUENCE if not exists hibernate_sequence
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

CREATE TABLE IF NOT EXISTS item
(
    id bigint NOT NULL,
    comhead character varying(4096),
    comment integer,
    postdate timestamp without time zone,
    hnid bigint,
    lastupdate timestamp without time zone,
    points integer,
    title character varying(4096),
    posturl character varying(4096),
    hnuser character varying(4096),
    CONSTRAINT item_pkey PRIMARY KEY (id),
    CONSTRAINT item_hnid_key UNIQUE (hnid)
);

CREATE TABLE IF NOT EXISTS statistic
(
    id bigint NOT NULL,
    stat_key character varying(255),
    stat_value character varying(255),
    CONSTRAINT statistic_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS subscription
(
    id bigint NOT NULL,
    activated boolean,
    activation_date timestamp without time zone,
    annually boolean,
    daily boolean,
    email character varying(255) NOT NULL,
    monthly boolean,
    next_send_day timestamp without time zone NOT NULL,
    next_send_month timestamp without time zone NOT NULL,
    next_send_week timestamp without time zone NOT NULL,
    next_send_year timestamp without time zone NOT NULL,
    subsuuid character varying(255) NOT NULL,
    subscription_date timestamp without time zone NOT NULL,
    timezone character varying(255) NOT NULL,
    weekly boolean,
    CONSTRAINT subscription_pkey PRIMARY KEY (id),
    CONSTRAINT subscription_email_key UNIQUE (email)
);