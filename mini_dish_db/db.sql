CREATE DATABASE mini_dish_db;

\c mini_dish_db

CREATE USER mini_dish_manager WITH PASSWORD '....';

GRANT ALL PRIVILEGES ON DATABASE mini_dish_db TO mini_dish_user;
