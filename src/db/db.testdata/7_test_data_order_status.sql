insert into order_status (id, id_order, status, creation_date) values
(1, 1, 'CREATED', '2025-02-25T10:00:00')
,(2, 1, 'CONFIRMED', '2025-02-25T10:15:00')
on conflict do nothing;

