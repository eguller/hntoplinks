create table if not exists items
(
  id             uuid primary key default gen_random_uuid(),
  hnid           bigint       not null,
  story_type     varchar(255) not null,
  author         varchar(255) not null,
  created_at     timestamp             default current_timestamp,
  url            text         not null,
  score          int          not null default 0,
  title          text         not null,
  comments_count int          not null default 0,
  version        bigint       not null default 0
);
