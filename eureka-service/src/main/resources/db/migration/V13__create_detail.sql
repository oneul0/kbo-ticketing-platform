create table p_payment_detail
(
    id              bigint auto_increment primary key,
    payment_id      bigint                      not null,
    cid             nvarchar(10)                null,
    tid             nvarchar(20)                null,
    discount_price  int                         not null,
    method          nvarchar(20)                not null,
    discount_type   nvarchar(20) default 'NONE' not null,
    discount_amount int                         not null,
    account_number  nvarchar(20)                null
);