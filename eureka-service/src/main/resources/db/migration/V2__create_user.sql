create table p_user
(
    id         bigint auto_increment primary key,
    email      nvarchar(50)                  not null,
    password   nvarchar(255)                 not null,
    username   nvarchar(50)                  not null,
    nickname   nvarchar(20)                  not null,
    birth      date                          not null,
    role       nvarchar(20) default 'NORMAL' not null,
    is_deleted boolean      default false    null,
    deleted_by bigint                        null,
    deleted_at datetime                      null,
    created_by bigint                        null,
    created_at datetime                      null,
    updated_by bigint                        null,
    updated_at datetime                      null
);