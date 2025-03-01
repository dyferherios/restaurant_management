CREATE TABLE stock_movement (
    id SERIAL PRIMARY KEY,
    ingredient_id VARCHAR(50) NOT NULL REFERENCES ingredient(id),
    movement_type VARCHAR(10) CHECK (movement_type IN ('IN', 'OUT')),
    quantity DOUBLE PRECISION NOT NULL CHECK (quantity > 0),
    unit "unit",
    movement_date TIMESTAMP NOT NULL DEFAULT now()
);
