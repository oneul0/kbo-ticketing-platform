create table p_payment
(
    id             bigint auto_increment primary key,
    user_id        bigint                not null,
    total_price    int                   not null,
    discount_price int                   null,
    status         nvarchar(20)          not null,
    type           nvarchar(20)          not null,
    is_deleted     boolean default false not null,
    deleted_by     bigint                null,
    deleted_at     datetime              null,
    created_by     bigint                null,
    created_at     datetime              null,
    updated_by     bigint                null,
    updated_at     datetime              null
);