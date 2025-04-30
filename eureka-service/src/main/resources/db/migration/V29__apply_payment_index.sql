CREATE INDEX idx_payment_deleted ON p_payment (is_deleted);
CREATE INDEX idx_payment_detail_payment ON p_payment_detail (payment_id);