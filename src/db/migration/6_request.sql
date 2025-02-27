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