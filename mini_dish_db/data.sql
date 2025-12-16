INSERT INTO dish (name, dish_type) VALUES
('Salade Fraîche', 'START'),
('Poulet Grillé', 'MAIN'),
('Riz aux légumes', 'MAIN'),
('Gâteaux aux chocolats', 'DESSERT'),
('Salade de fruits', 'DESSERT');

INSERT INTO ingredients (name, price, category, id_dish) VALUES
('Laitue', 8000.00, 'VEGETABLE', 1),
('Tomates', 600.00, 'VEGETABLE', 1),
('Poulet', 4500.00, 'ANIMAL', 2),
('Chocolat', 3000.00, 'OTHER', 4),
('Beurre', 2500.00, 'DAIRY', 4);