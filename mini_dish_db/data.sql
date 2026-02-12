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

INSERT INTO stock_movement (id_ingredient, quantity, type, unit, creation_datetime) VALUES
(1, 5.0, 'IN', 'KG', '2024-01-05 08:00'),
(1, 0.2, 'OUT', 'KG', '2024-01-06 12:00'),
(2, 4.0, 'IN', 'KG', '2024-01-05 08:00'),
(2, 0.15, 'OUT', 'KG', '2024-01-06 12:00'),
(3, 10.0, 'IN', 'KG', '2024-01-04 09:00'),
(3, 1.0, 'OUT', 'KG', '2024-01-06 12:00'),
(4, 3.0, 'IN', 'KG', '2024-01-05 10:00'),
(4, 0.3, 'OUT', 'KG', '2024-01-06 12:00'),
(5, 2.5, 'IN', 'KG', '2024-01-05 10:00'),
(5, 0.2, 'OUT', 'KG', '2024-01-06 12:00');

INSERT INTO sale (id, creation_datetime) VALUES
(1, '2024-02-01 14:30');

INSERT INTO orders (id, reference, payment_status, id_sale) VALUES
(1, 'ORD00201', 'PAID', 1),
(2, 'ORD00202', 'UNPAID', NULL);