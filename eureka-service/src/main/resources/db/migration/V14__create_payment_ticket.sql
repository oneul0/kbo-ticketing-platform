create table p_payment_ticket
(
    id         bigint auto_increment primary key,
    payment_id bigint       not null,
    ticket_no  nvarchar(50) not null,
    price      int          not null
);