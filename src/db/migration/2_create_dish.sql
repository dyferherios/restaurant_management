create table if not exists dish (
    id varchar(4) primary key,
    name varchar (20) unique,
    unit_price integer
);