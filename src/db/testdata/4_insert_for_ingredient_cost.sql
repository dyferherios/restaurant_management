insert into ingredient_cost (id, last_modification_date, unit_price, unit) values
    ((select id FROM ingredient where name = 'saucisse'), NOW(), 20, 'G'),
    ((select id from ingredient where name = 'oil'), NOW(), 10000, 'L'),
    ((select id from ingredient where name = 'egg'), NOW(), 1000, 'U'),
    ((select id from ingredient where name = 'brade'), NOW(), 1000, 'U')
;

INSERT INTO ingredient_cost (id, last_modification_date, unit_price, unit)
VALUES ((SELECT id FROM ingredient WHERE name = 'egg'), ARRAY[NOW()], ARRAY[1200.0], 'U');

INSERT INTO ingredient_cost (id, unit, last_modification_date, unit_price)
VALUES
    ('I003', 'U', ARRAY[NOW()], ARRAY[1200.0])
ON CONFLICT (id)
DO UPDATE
SET
    last_modification_date = ingredient_cost.last_modification_date || EXCLUDED.last_modification_date,
    unit_price = ingredient_cost.unit_price || EXCLUDED.unit_price;
