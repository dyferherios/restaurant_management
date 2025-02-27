insert into ingredient_cost (id, last_modification_date, unit_price, unit) values
    ((select id FROM ingredient where name = 'saucisse'), NOW(), 20, 'G'),
    ((select id from ingredient where name = 'oil'), NOW(), 10000, 'L'),
    ((select id from ingredient where name = 'egg'), NOW(), 1000, 'U'),
    ((select id from ingredient where name = 'brade'), NOW(), 1000, 'U')
;