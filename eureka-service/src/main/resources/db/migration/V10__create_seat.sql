create table baseball.p_seat
(
    id          bigint auto_increment primary key,
    name        varchar(30)          not null,
    seat_block  int                   not null,
    seat_column int                   not null,
    seat_row    int                   not null,
    seat_no     varchar(10)          not null,
    price       int                   not null,
    is_active   boolean default true  not null,
    is_senior   boolean default false not null
) default character set utf8mb4;