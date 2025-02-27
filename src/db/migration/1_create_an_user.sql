-- Step 1: Drop the user if it already exists
drop user IF EXISTS dyferherios;

-- Step 2: Create the user with the desired password
create user dyferherios with ENCRYPTED PASSWORD System.getenv("DB_USER");
    
\du --verify role

-- Step 3: Drop the database if it exists (optional, if recreating the DB is needed)
drop database IF EXISTS restaurant_management;

-- Step 4: Create the database
create DATABASE restaurant_management;

-- Step 5: Grant the user permission to connect to the database
grant connect on DATABASE restaurant_management TO dyferherios;

-- Step 6: Grant usage on a specific schema (replace `schema` with your schema name)
grant USAGE on SCHEMA public TO dyferherios;

\dn --check shema
-- Step 7: Grant table-level privileges (replace `schema` with your schema name)
grant
select, insert,
update, delete on ALL TABLES IN SCHEMA public TO dyferherios;

grant USAGE on ALL SEQUENCES IN SCHEMA public TO dyferherios;

grant REFERENCES on ALL TABLES IN SCHEMA public TO dyferherios;

-- Step 8: (Optional) Automatically grant privileges for future tables in the schema
alter DEFAULT PRIVILEGES IN SCHEMA public
grant
select, insert,
update, delete on TABLES to dyferherios;


-- Find the process IDs of the sessions connected to the database:
-- SELECT pid, usename, application_name
-- FROM pg_stat_activity
-- WHERE datname = 'library_management';


-- Terminate the sessions using the pg_terminate_backend() function:
-- SELECT pg_terminate_backend(pid)
--FROM pg_stat_activity
--WHERE datname = 'library_management';