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


