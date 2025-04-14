ALTER TABLE p_membership
    ADD available_quantity int not null default 0;

UPDATE p_membership
SET available_quantity = 40
WHERE season = 2025
  AND name = 'GOLD';

UPDATE p_membership
SET available_quantity = 70
WHERE season = 2025
  AND name = 'VIP';

UPDATE p_membership
SET available_quantity = 100
WHERE season = 2025
  AND name = 'SVIP';