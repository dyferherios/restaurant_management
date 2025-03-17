do
$$
    begin
        if not exists(select from pg_type where typname = 'order_status_process') then
            create type order_status_process as enum ('CREATED', 'CONFIRMED', 'INPROGRESS', 'FINISHED', 'DELIVERED');
        end if;
    end
$$;

create table order_status(
    id bigserial primary key,
    id_order bigint,
    status order_status_process,
    creation_date timestamp
)





--enum order_process_status
--inprogress