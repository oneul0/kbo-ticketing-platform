create table p_membership_user
(
    id            bigint auto_increment primary key,
    user_id       bigint                not null,
    membership_id bigint                not null,
    is_active     boolean default true  not null,
    is_deleted    boolean default false not null,
    deleted_by    bigint                null,
    deleted_at    datetime              null,
    created_by    bigint                null,
    created_at    datetime              null,
    updated_by    bigint                null,
    updated_at    datetime              null
);