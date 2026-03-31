CREATE TYPE category_enum AS ENUM ('VEGETABLE', 'ANIMAL', 'MARINE', 'DAIRY', 'OTHER');

CREATE TYPE dish_type_enum AS ENUM ('START', 'MAIN', 'DESSERT');

-- Table des ingrédients
CREATE TABLE ingredient (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    price NUMERIC(10,2) NOT NULL,
    category VARCHAR(50) NOT NULL
);

-- Table des plats
CREATE TABLE dish (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    dish_type VARCHAR(50) NOT NULL,
    sale_price NUMERIC(10,2) NOT NULL
);

-- Table de jointure ManyToMany entre dish et ingredient
CREATE TABLE dish_ingredient (
    dish_id INT NOT NULL,
    ingredient_id INT NOT NULL,
    PRIMARY KEY (dish_id, ingredient_id),
    FOREIGN KEY (dish_id) REFERENCES dish(id) ON DELETE CASCADE,
    FOREIGN KEY (ingredient_id) REFERENCES ingredient(id) ON DELETE CASCADE
);

-- Table de stock pour gérer les valeurs de stock par ingrédient
CREATE TABLE stock (
    id SERIAL PRIMARY KEY,
    ingredient_id INT NOT NULL,
    date DATE NOT NULL,
    unit VARCHAR(10) NOT NULL, -- PCS, KG, L
    value NUMERIC(10,2) NOT NULL,
    FOREIGN KEY (ingredient_id) REFERENCES ingredient(id) ON DELETE CASCADE
);

CREATE TABLE stock_movement (
    id SERIAL PRIMARY KEY,
    ingredient_id INT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    unit VARCHAR(10) NOT NULL,
    value NUMERIC(10,2) NOT NULL,
    type VARCHAR(10) NOT NULL, -- ENTRY ou EXIT
    FOREIGN KEY (ingredient_id) REFERENCES ingredient(id) ON DELETE CASCADE
);


