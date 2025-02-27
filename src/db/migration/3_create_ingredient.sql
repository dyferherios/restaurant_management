DO
$$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'unit') THEN
        CREATE TYPE "UNIT" AS ENUM ('G', 'L', 'U');
    END IF;
END;
$$;

create table if not exists ingredient (
    id varchar(4) primary key,
    name varchar(20) unique
);

CREATE TABLE IF NOT EXISTS ingredient_cost(
    id varchar(4) references ingredient(id) on delete cascade,
    last_modification_date TIMESTAMP,
    unit_price float,
    unit "UNIT"
);

ALTER TABLE ingredient_cost
ADD CONSTRAINT unique_ingredient_cost UNIQUE (id);
