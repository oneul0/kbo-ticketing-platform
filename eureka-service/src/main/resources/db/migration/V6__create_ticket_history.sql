create table p_ticket_history
(
    id         bigint auto_increment primary key,
    ticket_id  bigint       not null,
    match_id   bigint       not null,
    seat_id    bigint       not null,
    user_id    bigint       not null,
    status     varchar(20) not null,
    created_at datetime     not null
) default character set utf8mb4;