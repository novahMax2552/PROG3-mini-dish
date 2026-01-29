CREATE TYPE category_enum AS ENUM ('VEGETABLE', 'ANIMAL', 'MARINE', 'DAIRY', 'OTHER');

CREATE TYPE dish_type_enum AS ENUM ('START', 'MAIN', 'DESSERT');

CREATE TABLE Dish (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    dish_type dish_type_enum NOT NULL
);

CREATE TABLE Ingredient (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    price NUMERIC(10,2) NOT NULL,
    category category_enum NOT NULL,
    id_dish INT REFERENCES Dish(id) ON DELETE SET NULL
);

ALTER TABLE ingredient
ADD COLUMN IF NOT EXISTS required_quantity DOUBLE PRECISION NULL;
