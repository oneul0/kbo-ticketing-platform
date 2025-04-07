create table p_payment_membership
(
    id                 bigint auto_increment primary key,
    payment_id         bigint not null,
    membership_user_id bigint not null,
    price              int    not null
);