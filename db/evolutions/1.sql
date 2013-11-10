

# --- !Ups

CREATE TABLE item
(
  id bigint NOT NULL,
  comhead character varying(255),
  comment integer,
  postdate timestamp without time zone,
  hnid bigint,
  lastupdate timestamp without time zone,
  points integer,
  title character varying(255),
  posturl character varying(255),
  hnuser character varying(255),
  CONSTRAINT item_pkey PRIMARY KEY (id)
);

# --- !Downs

DROP TABLE item;
