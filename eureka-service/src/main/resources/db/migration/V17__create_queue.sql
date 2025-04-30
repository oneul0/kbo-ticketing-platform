create table p_queue
(
    id         bigint auto_increment primary key,
    store_id   bigint                         not null,
    user_id    bigint                         not null,
    sequence   int                            not null,
    status     varchar(20) default 'PENDING' not null,
    cancel_reason varchar(20) default null,
    is_deleted boolean      default false     not null,
    deleted_by bigint                         null,
    deleted_at datetime                       null,
    created_by bigint                         null,
    created_at datetime                       null,
    updated_by bigint                         null,
    updated_at datetime                       null
) default character set utf8mb4;