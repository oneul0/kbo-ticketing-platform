create table p_payment_detail
(
    id              bigint auto_increment primary key,
    payment_id      bigint                      not null,
    cid             varchar(10)                null,
    tid             varchar(20)                null,
    discount_price  int                         not null,
    method          varchar(20)                not null,
    discount_amount int                         not null,
    account_number  varchar(20)                null
) default character set utf8mb4;