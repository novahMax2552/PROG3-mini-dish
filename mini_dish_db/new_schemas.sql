ALTER TABLE dish
ADD COLUMN IF NOT EXISTS selling_price NUMERIC(10,2) NULL;

ALTER TABLE ingredient
DROP COLUMN IF EXISTS id_dish;

ALTER TABLE ingredient
ADD CONSTRAINT uq_ingredient_name UNIQUE (name);

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'unit_type') THEN
        CREATE TYPE unit_type AS ENUM ('PCS', 'KG', 'L');
    END IF;
END$$;

CREATE TABLE IF NOT EXISTS dish_ingredient (
    id SERIAL PRIMARY KEY,
    id_dish INT NOT NULL,
    id_ingredient INT NOT NULL,
    quantity_required NUMERIC(10,2) NOT NULL,
    unit unit_type NOT NULL,

    CONSTRAINT fk_dish FOREIGN KEY (id_dish) REFERENCES dish(id) ON DELETE CASCADE,
    CONSTRAINT fk_ingredient FOREIGN KEY (id_ingredient) REFERENCES ingredient(id) ON DELETE CASCADE,
    CONSTRAINT uq_dish_ingredient UNIQUE (id_dish, id_ingredient)
);

INSERT INTO dish (name, dish_type, selling_price) VALUES
('Salade Fraîche', 'START', 12000.00),
('Poulet Grillé', 'MAIN', 18000.00),
('Riz aux légumes', 'MAIN', 10000.00),
('Gâteau au chocolat', 'DESSERT', 15000.00),
('Salade de fruits', 'DESSERT', 13000.00),
('Salade de tomates', 'START', 9000.00);

INSERT INTO ingredient (name, price, category) VALUES
('Huile', 2000.00, 'OTHER');

UPDATE dish SET selling_price = 3500.00 WHERE id = 1;
UPDATE dish SET selling_price = 12000.00 WHERE id = 2;
UPDATE dish SET selling_price = NULL WHERE id = 3;
UPDATE dish SET selling_price = 8000.00 WHERE id = 4;
UPDATE dish SET selling_price = NULL WHERE id = 5;

INSERT INTO dish_ingredient (id_dish, id_ingredient, quantity_required, unit) VALUES
(1, 1, 0.20, 'KG'),
(1, 2, 0.15, 'KG'),
(2, 3, 1.00, 'KG'),
(4, 4, 0.30, 'KG'),
(4, 5, 0.20, 'KG');
