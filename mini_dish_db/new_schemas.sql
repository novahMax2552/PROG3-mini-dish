ALTER TABLE ingredients DROP COLUMN IF EXISTS id_dish;
ALTER TABLE ingredients DROP COLUMN IF EXISTS required_quantity;
ALTER TABLE ingredients DROP COLUMN IF EXISTS Unit;
ALTER TABLE dish ADD COLUMN IF NOT EXISTS price numeric(10,2) NULL;

CREATE TABLE DishIngredient (
    id SERIAL PRIMARY KEY,
    dish_id INT NOT NULL,
    ingredient_id INT NOT NULL,
    required_quantity numeric(10,2) NULL,
    unit VARCHAR(20) NULL,
    FOREIGN KEY (dish_id) REFERENCES dish(id),
    FOREIGN KEY (ingredient_id) REFERENCES ingredients(id)
);

INSERT INTO DishIngredient (dish_id, ingredient_id, required_quantity, unit) VALUES
(1, 1, 0.20, 'kg'),
(1, 2, 0.15, 'kg'),
(2, 3, 1, 'kg'),
(4, 4, 0.30, 'kg'),
(4, 5, 0.20, 'kg');


