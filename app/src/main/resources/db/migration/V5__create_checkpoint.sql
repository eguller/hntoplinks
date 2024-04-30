create table if not exists checkpoints
(
  name         varchar(255) not null,
  checkpoint   bigint       not null,
  last_updated timestamp    not null default current_timestamp,
  version      bigint       not null default 0
);

insert into checkpoints (name, checkpoint)
values ('stories', 0);
