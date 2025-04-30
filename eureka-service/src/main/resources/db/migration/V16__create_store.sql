create table p_store
(
    id         bigint auto_increment primary key,
    stadium_id bigint                not null,
    name       varchar(50)          not null,
    open_at    datetime              not null,
    closed_at  datetime              not null,
    is_closed  boolean default false not null,
    is_deleted boolean default false null,
    deleted_by bigint                null,
    deleted_at datetime              null,
    created_by bigint                null,
    created_at datetime              null,
    updated_by bigint                null,
    updated_at datetime              null
) default character set utf8mb4;