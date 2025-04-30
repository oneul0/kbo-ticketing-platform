create table p_user
(
    id         bigint auto_increment primary key,
    email      varchar(50)                  not null,
    password   varchar(255)                 not null,
    username   varchar(50)                  not null,
    nickname   varchar(20)                  not null,
    birth      date                          not null,
    role       varchar(20) default 'NORMAL' not null,
    is_deleted boolean      default false    null,
    deleted_by bigint                        null,
    deleted_at datetime                      null,
    created_by bigint                        null,
    created_at datetime                      null,
    updated_by bigint                        null,
    updated_at datetime                      null
) default character set utf8mb4;