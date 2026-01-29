INSERT INTO Dish (id, name, dish_type) VALUES
(1, 'Salade fraîche', 'START'),
(2, 'Poulet grillé', 'MAIN'),
(3, 'Riz aux légumes', 'MAIN'),
(4, 'Gâteau au chocolat', 'DESSERT'),
(5, 'Salade de fruits', 'DESSERT');

INSERT INTO Ingredient (id, name, price, category, id_dish) VALUES
(1, 'Laitue', 800.00, 'VEGETABLE', 1),
(2, 'Tomate', 600.00, 'VEGETABLE', 1),
(3, 'Poulet', 4500.00, 'ANIMAL', 2),
(4, 'Chocolat', 3000.00, 'OTHER', 4),
(5, 'Beurre', 2500.00, 'DAIRY', 4);

UPDATE ingredient SET required_quantity = 1 WHERE name = 'Laitue';
UPDATE ingredient SET required_quantity = 2 WHERE name = 'Tomate';
UPDATE ingredient SET required_quantity = 0.5 WHERE name = 'Poulet';
UPDATE ingredient SET required_quantity = NULL WHERE name = 'Chocolat';
UPDATE ingredient SET required_quantity = NULL WHERE name = 'Beurre';
