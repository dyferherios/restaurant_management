create table if not exists dish_ingredient (
    dish_id varchar(4) references dish(id) on delete cascade,
    ingredient_id varchar(4)  references ingredient(id) on delete cascade,
    quantity float,
    primary key (dish_id, ingredient_id)
);
