INSERT INTO stock_movement (ingredient_id, movement_type, quantity, unit, movement_date) VALUES
('I001', 'IN', 10000, 'U', '2025-02-01 08:00:00'),
('I002', 'IN', 20, 'U', '2025-02-01 08:00:00'),
('I003', 'IN', 100, 'G', '2025-02-01 08:00:00'),
('I004', 'IN', 50, 'L', '2025-02-01 08:00:00');

INSERT INTO stock_movement (ingredient_id, movement_type, quantity, unit, movement_date) VALUES
('I003', 'OUT', 10, 'U', '2025-02-02 10:00:00'),
('I003', 'OUT', 10, 'U', '2025-02-03 15:00:00'),
('I004', 'OUT', 20, 'G', '2025-02-05 16:00:00');

INSERT INTO stock_movement (ingredient_id, movement_type, quantity, unit, movement_date) VALUES
('I003', 'OUT', 10, 'U', '2025-02-25 10:00:00'),

