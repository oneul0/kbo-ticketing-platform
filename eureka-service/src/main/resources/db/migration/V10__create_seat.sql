create table p_seat
(
    id          bigint auto_increment primary key,
    name        nvarchar(30)         not null,
    seat_block  int                  not null,
    seat_column int                  not null,
    seat_row    int                  not null,
    seat_no     nvarchar(10)         not null,
    price       int                  not null,
    is_active   boolean default true not null
);