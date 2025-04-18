create table p_queue
(
    id         bigint auto_increment primary key,
    store_id   bigint                         not null,
    user_id    bigint                         not null,
    sequence   int                            not null,
    status     nvarchar(20) default 'PENDING' not null,
    is_deleted boolean      default false     not null,
    deleted_by bigint                         null,
    deleted_at datetime                       null,
    created_by bigint                         null,
    created_at datetime                       null,
    updated_by bigint                         null,
    updated_at datetime                       null
);