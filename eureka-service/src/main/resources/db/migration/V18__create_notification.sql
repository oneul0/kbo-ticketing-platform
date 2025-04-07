create table p_notification
(
    id         bigint auto_increment primary key,
    user_id    bigint                not null,
    email      nvarchar(50)          not null,
    type       nvarchar(30)          not null,
    content    nvarchar(255)         not null,
    is_deleted boolean default false not null,
    deleted_by bigint                null,
    deleted_at datetime              null,
    created_by bigint                null,
    created_at datetime              null,
    updated_by bigint                null,
    updated_at datetime              null
);