CREATE TABLE ingredients (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price numeric(10,2) NOT NULL,
    category VARCHAR(50) NOT NULL,
    id_dish INT,
    FOREIGN KEY (id_dish) REFERENCES dishe(id)
)

CREATE TABLE dishe (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    dish_type VARCHAR(50) NOT NULL,
)

ALTER TABLE ingredients ADD COLUMN required_quantity INT NULL ;