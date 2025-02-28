select ingredient_cost.id, ingredient.name, ingredient_cost.unit_price, ingredient_cost.unit, ingredient_cost.last_modification_date
from ingredient
inner join ingredient_cost on ingredient.id=ingredient_cost.id;

select ingredient.name, dish_ingredient.quantity, dish_ingredient.dish_id, dish_ingredient.ingredient_id from dish_ingredient join ingredient on ingredient.id=dish_ingredient.ingredient_id;


select ingredient.name, ingredient_cost.id,  ingredient_cost.unit_price, ingredient_cost.unit, ingredient_cost.last_modification_date, dish_ingredient.quantity, dish_ingredient.dish_id from ingredient
inner join ingredient_cost on ingredient.id=ingredient_cost.id join dish_ingredient on ingredient.id=dish_ingredient.ingredient_id;

--change type to array
ALTER TABLE ingredient_cost
ADD COLUMN last_modification_date_tmp TIMESTAMP[];

UPDATE ingredient_cost
SET last_modification_date_tmp = ARRAY[last_modification_date];

ALTER TABLE ingredient_cost
DROP COLUMN last_modification_date;

ALTER TABLE ingredient_cost
RENAME COLUMN last_modification_date_tmp TO last_modification_date;

--change type to array
ALTER TABLE ingredient_cost
ADD COLUMN unit_price_tmp float[];

UPDATE ingredient_cost
SET unit_price_tmp = ARRAY[unit_price];

ALTER TABLE ingredient_cost
DROP COLUMN unit_price;

ALTER TABLE ingredient_cost
RENAME COLUMN unit_price_tmp TO unit_price;

SELECT ingredient.id, ingredient.name,
       MIN(price) AS unit_price
FROM ingredient
INNER JOIN ingredient_cost ON ingredient.id = ingredient_cost.id
INNER JOIN LATERAL unnest(ingredient_cost.unit_price) AS price ON true
WHERE ingredient.name ILIKE '%i%'
GROUP BY ingredient.id, ingredient.name
HAVING MIN(price) > 1000
ORDER BY ingredient.name ASC, unit_price DESC;

SELECT ic.id, i.name,
       ic.unit_price[array_length(ic.unit_price, 1)] AS last_unit_price,
       ic.unit,
       ic.last_modification_date[array_length(ic.last_modification_date, 1)] AS last_modification_date
FROM ingredient i
INNER JOIN ingredient_cost ic ON i.id = ic.id
WHERE ic.last_modification_date[array_length(ic.last_modification_date, 1)] > '2025-02-26 00:00:00'
  AND i.name ILIKE '%e%'
ORDER BY last_modification_date DESC
LIMIT 10 OFFSET 0;

SELECT ic.id, i.name,
       ic.unit_price[array_length(ic.unit_price, 1)] AS last_unit_price,
       ic.unit,
       ic.last_modification_date[array_length(ic.last_modification_date, 1)] AS last_modification_date
FROM ingredient i
INNER JOIN ingredient_cost ic ON i.id = ic.id
WHERE ic.last_modification_date[array_length(ic.last_modification_date, 1)] > '2025-02-25 00:00:00'
  AND i.name ILIKE '%e%'
  AND ic.unit_price[array_length(ic.unit_price, 1)] >= 1000
  AND ic.unit = 'U'
ORDER BY last_modification_date DESC
LIMIT 10 OFFSET 0;

