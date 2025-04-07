create table p_ticket
(
    id         bigint auto_increment primary key,
    match_id   bigint                         not null,
    seat_id    bigint                         not null,
    user_id    bigint                         not null,
    ticket_no  nvarchar(50)                   not null,
    status     nvarchar(20) default 'PENDING' not null,
    price      int                            not null,
    is_deleted boolean      default false     not null,
    deleted_by bigint                         null,
    deleted_at datetime                       null,
    created_by bigint                         null,
    created_at datetime                       null,
    updated_by bigint                         null,
    updated_at datetime                       null
);