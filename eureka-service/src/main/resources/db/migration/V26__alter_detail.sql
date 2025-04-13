ALTER TABLE p_payment_detail
    ADD account_bank varchar(30) null;

ALTER TABLE p_payment_detail
    ADD due_date datetime null;

ALTER TABLE p_payment_detail
    ADD account_holder varchar(20) null;