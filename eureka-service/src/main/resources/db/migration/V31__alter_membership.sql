ALTER TABLE p_membership
    ADD price int not null default 0;

UPDATE p_membership
SET price = 1000000
WHERE name = 'GOLD';

UPDATE p_membership
SET price = 2000000
WHERE name = 'VIP';

UPDATE p_membership
SET price = 3000000
WHERE name = 'SVIP';