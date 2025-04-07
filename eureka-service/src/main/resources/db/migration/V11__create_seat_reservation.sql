create table p_seat_reservation
(
    id               bigint auto_increment primary key,
    seat_id          bigint                not null,
    user_id          bigint                not null,
    reservation_date date                  not null,
    is_reserved      boolean default false not null,
    is_deleted       boolean default false not null,
    deleted_by       bigint                null,
    deleted_at       datetime              null,
    created_by       bigint                null,
    created_at       datetime              null,
    updated_by       bigint                null,
    updated_at       datetime              null
);