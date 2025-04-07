create table p_membership
(
    id         bigint auto_increment primary key,
    season     year                  not null,
    name       nvarchar(20)          not null,
    discount   double                not null,
    is_deleted boolean default false not null,
    deleted_by bigint                null,
    deleted_at datetime              null,
    created_by bigint                null,
    created_at datetime              null,
    updated_by bigint                null,
    updated_at datetime              null
);