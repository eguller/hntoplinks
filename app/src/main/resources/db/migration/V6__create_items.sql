create table if not exists items
(
  id          bigint not null,
  by          varchar(255),
  descendants int       not null default 0,
  score       int       not null default 0,
  time        timestamptz default current_timestamp,
  title       text,
  type        varchar(255),
  url         text,
  CONSTRAINT item_id_key UNIQUE (id)
);
