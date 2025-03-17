create table dish_order_status(
    id bigserial primary key,
    id_dish_order bigint,
    id_order_status bigint,
    constraint fk_stock_movement_id_dish_order foreign key (id_dish_order) references dish_order (id),
    constraint fk_stock_movement_id_dish_order_status foreign key (id_order_status) references order_status (id)
);
