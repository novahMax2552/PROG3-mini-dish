package com.example.demo;

import java.sql.*;
import java.time.Instant;
import java.util.*;

import com.example.demo.entity.Dish;
import com.example.demo.entity.Ingredients;

public class DataRetriever {

    public DataRetriever() {
        // constructeur vide
    }

    public Dish findDishById(int id) throws SQLException {
        Dish dish = null;
        try (Connection conn = DBConnection.getDBConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT id, name, dish_type, selling_price FROM dish WHERE id=?");
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                dish = new Dish(
                    rs.getInt("id"),
                    rs.getString("name"),
                    DishType.valueOf(rs.getString("dish_type")),
                    rs.getDouble("selling_price")
                );
                dish.setIngredients(findDishIngredientsByDishId(id));
            } else {
                throw new RuntimeException("Plat introuvable avec id=" + id);
            }
        }
        return dish;
    }

    private List<Ingredients> findDishIngredientsByDishId(int dishId) throws SQLException {
        List<Ingredients> ingredients = new ArrayList<>();
        try (Connection conn = DBConnection.getDBConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT i.id, i.name, i.price, i.category " +
                "FROM dish_ingredient di " +
                "JOIN ingredient i ON di.id_ingredient = i.id " +
                "WHERE di.id_dish=?");
            stmt.setInt(1, dishId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Ingredients ing = new Ingredients(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    CategoryEnum.valueOf(rs.getString("category"))
                );
                ingredients.add(ing);
            }
        }
        return ingredients;
    }

    public List<Ingredients> findIngredients(int page, int size) throws SQLException {
        List<Ingredients> ingredients = new ArrayList<>();
        try (Connection conn = DBConnection.getDBConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT i.id, i.name, i.price, i.category " +
                "FROM ingredient i ORDER BY i.id LIMIT ? OFFSET ?");
            stmt.setInt(1, size);
            stmt.setInt(2, (page - 1) * size);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Ingredients ing = new Ingredients(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    CategoryEnum.valueOf(rs.getString("category"))
                );
                ingredients.add(ing);
            }
        }
        return ingredients;
    }

    public Dish saveDish(Dish dish) throws SQLException {
        try (Connection conn = DBConnection.getDBConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1. Insérer ou mettre à jour le plat
                if (dish.getId() == null) {
                    String insertQuery = "INSERT INTO dish(name, dish_type, selling_price) VALUES (?, ?::dish_type_enum, ?) RETURNING id";
                    try (PreparedStatement insert = conn.prepareStatement(insertQuery)) {
                        insert.setString(1, dish.getName());
                        insert.setObject(2, dish.getDishType().name(), java.sql.Types.OTHER);
                        if (dish.getSellingPrice() != null) {
                            insert.setDouble(3, dish.getSellingPrice());
                        } else {
                            insert.setNull(3, java.sql.Types.NUMERIC);
                        }
                        try (ResultSet rs = insert.executeQuery()) {
                            if (rs.next()) {
                                dish.setId(rs.getInt("id"));
                            }
                        }
                    }
                } else {
                    String updateQuery = "UPDATE dish SET name=?, dish_type=?::dish_type_enum, selling_price=? WHERE id=?";
                    try (PreparedStatement update = conn.prepareStatement(updateQuery)) {
                        update.setString(1, dish.getName());
                        update.setObject(2, dish.getDishType().name(), java.sql.Types.OTHER);
                        if (dish.getSellingPrice() != null) {
                            update.setDouble(3, dish.getSellingPrice());
                        } else {
                            update.setNull(3, java.sql.Types.NUMERIC);
                        }
                        update.setInt(4, dish.getId());
                        update.executeUpdate();
                    }
                }

                // 2. Supprimer les anciennes liaisons
                String clearQuery = "DELETE FROM dish_ingredient WHERE id_dish=?";
                try (PreparedStatement clear = conn.prepareStatement(clearQuery)) {
                    clear.setInt(1, dish.getId());
                    clear.executeUpdate();
                }

                // 3. Insérer les nouvelles liaisons
                String linkQuery = "INSERT INTO dish_ingredient(id_dish, id_ingredient, quantity_required, unit) VALUES (?, ?, ?, ?::unit_type)";
                try (PreparedStatement link = conn.prepareStatement(linkQuery)) {
                    for (Ingredients ing : dish.getIngredients()) {
                        if (ing.getId() == null) {
                            String insertIngredient = "INSERT INTO ingredient(name, price, category) VALUES (?, ?, ?::category_enum) RETURNING id";
                            try (PreparedStatement insertIng = conn.prepareStatement(insertIngredient)) {
                                insertIng.setString(1, ing.getName());
                                insertIng.setDouble(2, ing.getPrice());
                                insertIng.setObject(3, ing.getCategory().name(), java.sql.Types.OTHER);
                                try (ResultSet rsIng = insertIng.executeQuery()) {
                                    if (rsIng.next()) {
                                        ing.setId(rsIng.getInt("id"));
                                    }
                                }
                            }
                        }
                        link.setInt(1, dish.getId());
                        link.setInt(2, ing.getId());
                        link.setDouble(3, 1.0); // ⚠️ quantité par défaut, à adapter
                        link.setString(4, "KG");
                        link.executeUpdate();
                    }
                }

                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
        return dish;
    }

    public List<Dish> findDishsByIngredientName(String ingredientName) throws SQLException {
    List<Dish> dishes = new ArrayList<>();
    try (Connection conn = DBConnection.getDBConnection()) {
        PreparedStatement stmt = conn.prepareStatement(
            "SELECT DISTINCT d.id, d.name, d.dish_type " +
            "FROM dish d " +
            "JOIN dish_ingredient di ON d.id = di.id_dish " +
            "JOIN ingredient i ON di.id_ingredient = i.id " +
            "WHERE i.name ILIKE ?");
        stmt.setString(1, "%" + ingredientName + "%");
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            dishes.add(new Dish(
                rs.getInt("id"),
                rs.getString("name"),
                DishType.valueOf(rs.getString("dish_type"))
            ));
        }
    }
    return dishes;
}

public List<Ingredients> findIngredientsByCriteria(String ingredientName, CategoryEnum category, String dishName, int page, int size) throws SQLException {
    List<Ingredients> ingredients = new ArrayList<>();
    StringBuilder query = new StringBuilder(
        "SELECT i.id, i.name, i.price, i.category " +
        "FROM ingredient i " +
        "LEFT JOIN dish_ingredient di ON i.id = di.id_ingredient " +
        "LEFT JOIN dish d ON di.id_dish = d.id WHERE 1=1 ");
    List<Object> params = new ArrayList<>();

    if (ingredientName != null) {
        query.append("AND i.name ILIKE ? ");
        params.add("%" + ingredientName + "%");
    }
    if (category != null) {
        query.append("AND i.category = ?::category_enum ");
        params.add(category.name());
    }
    if (dishName != null) {
        query.append("AND d.name ILIKE ? ");
        params.add("%" + dishName + "%");
    }

    query.append("LIMIT ? OFFSET ?");
    params.add(size);
    params.add((page - 1) * size);

    try (Connection conn = DBConnection.getDBConnection();
         PreparedStatement stmt = conn.prepareStatement(query.toString())) {
        for (int i = 0; i < params.size(); i++) {
            stmt.setObject(i + 1, params.get(i));
        }
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Ingredients ing = new Ingredients(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getDouble("price"),
                CategoryEnum.valueOf(rs.getString("category"))
            );
            ingredients.add(ing);
        }
    }
    return ingredients;
}

public List<Ingredients> createIngredients(List<Ingredients> newIngredients) {
    List<Ingredients> created = new ArrayList<>();
    try (Connection conn = DBConnection.getDBConnection()) {
        conn.setAutoCommit(false);
        try {
            String checkQuery = "SELECT id FROM ingredient WHERE name = ?";
            String insertQuery = "INSERT INTO ingredient (name, price, category) VALUES (?, ?, ?::category_enum) RETURNING id";

            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
                 PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {

                for (Ingredients ing : newIngredients) {
                    checkStmt.setString(1, ing.getName());
                    try (ResultSet rs = checkStmt.executeQuery()) {
                        if (rs.next()) {
                            ing.setId(rs.getInt("id"));
                            created.add(ing);
                            continue;
                        }
                    }

                    insertStmt.setString(1, ing.getName());
                    insertStmt.setDouble(2, ing.getPrice());
                    insertStmt.setObject(3, ing.getCategory().name(), java.sql.Types.OTHER);

                    try (ResultSet rs = insertStmt.executeQuery()) {
                        if (rs.next()) {
                            ing.setId(rs.getInt("id"));
                            created.add(ing);
                        }
                    }
                }
            }

            conn.commit();
        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    } catch (SQLException e) {
        throw new RuntimeException("Erreur SQL : " + e.getMessage(), e);
    }
    return created;
}

    // ------------------- INGREDIENT & STOCK -------------------

    public Ingredients saveIngredient(Ingredients toSave, List<StockMovement> movements) throws SQLException {
        try (Connection conn = DBConnection.getDBConnection()) {
            conn.setAutoCommit(false);
            try {
                // Vérifier si l’ingrédient existe déjà
                if (toSave.getId() == null) {
                    String checkQuery = "SELECT id FROM ingredient WHERE name = ?";
                    try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                        checkStmt.setString(1, toSave.getName());
                        try (ResultSet rs = checkStmt.executeQuery()) {
                            if (rs.next()) {
                                toSave.setId(rs.getInt("id"));
                            } else {
                                String insertQuery = "INSERT INTO ingredient(name, price, category) VALUES (?, ?, ?::category_enum) RETURNING id";
                                try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                                    insertStmt.setString(1, toSave.getName());
                                    insertStmt.setDouble(2, toSave.getPrice());
                                    insertStmt.setObject(3, toSave.getCategory().name(), java.sql.Types.OTHER);
                                    try (ResultSet rsInsert = insertStmt.executeQuery()) {
                                        if (rsInsert.next()) {
                                            toSave.setId(rsInsert.getInt("id"));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Insérer les mouvements de stock
                String insertMovement = "INSERT INTO stock_movement(id, id_ingredient, quantity, type, unit, creation_datetime) " +
                                        "VALUES (?, ?, ?, ?::movement_type, ?::unit_type, ?) ON CONFLICT DO NOTHING";
                try (PreparedStatement movementStmt = conn.prepareStatement(insertMovement)) {
                    for (StockMovement movement : movements) {
                        if (movement.getId() != null) {
                            movementStmt.setInt(1, movement.getId());
                            movementStmt.setInt(2, toSave.getId());
                            movementStmt.setDouble(3, movement.getValue().getQuantity());
                            movementStmt.setString(4, movement.getType().name());
                            movementStmt.setString(5, movement.getValue().getUnit().name());
                            movementStmt.setTimestamp(6, Timestamp.from(movement.getCreationDatetime()));
                            movementStmt.executeUpdate();
                        }
                    }
                }

                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
        return toSave;
    }

public StockValue getStockValueAt(int ingredientId, Instant instant) throws SQLException {
    String query = "SELECT unit, SUM(CASE WHEN type = 'OUT' THEN -quantity ELSE quantity END) AS actual_quantity " +
                   "FROM stock_movement " +
                   "WHERE id_ingredient = ? AND creation_datetime <= ? " +
                   "GROUP BY unit";

    try (Connection conn = DBConnection.getDBConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setInt(1, ingredientId);
        stmt.setTimestamp(2, Timestamp.from(instant));
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            double total = rs.getDouble("actual_quantity");
            UnitType unit = UnitType.valueOf(rs.getString("unit"));
            return new StockValue(total, unit);
        }
    }
    return new StockValue(0.0, UnitType.KG);
}

    public List<StockMovement> findStockMovementsByIngredient(int ingredientId) throws SQLException {
        List<StockMovement> movements = new ArrayList<>();
        String query = "SELECT id, quantity, type, unit, creation_datetime FROM stock_movement WHERE id_ingredient = ?";
        try (Connection conn = DBConnection.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, ingredientId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                StockValue value = new StockValue(rs.getDouble("quantity"), UnitType.valueOf(rs.getString("unit")));
                MovementTypeEnum type = MovementTypeEnum.valueOf(rs.getString("type"));
                Instant date = rs.getTimestamp("creation_datetime").toInstant();
                movements.add(new StockMovement(rs.getInt("id"), value, type, date));
            }
        }
        return movements;
    }
    public Order saveOrder(Order orderToSave) throws SQLException {
    try (Connection conn = DBConnection.getDBConnection()) {
        conn.setAutoCommit(false);
        try {
            // Vérification des stocks
            for (DishOrder dishOrder : orderToSave.getDishOrders()) {
                Dish dish = dishOrder.getDish();
                for (Ingredients ing : dish.getIngredients()) {
                    StockValue stock = getStockValueAt(ing.getId(), Instant.now());
                    double required = dishOrder.getQuantity(); // simplifié : 1 unité par plat
                    if (stock.getQuantity() < required) {
                        throw new RuntimeException("Stock insuffisant pour l’ingrédient : " + ing.getName());
                    }
                }
            }

            // Génération de la référence
            String refQuery = "SELECT COUNT(*) FROM orders";
            int count = 0;
            try (PreparedStatement stmt = conn.prepareStatement(refQuery);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) count = rs.getInt(1);
            }
            String reference = String.format("ORD%05d", count + 1);
            orderToSave.setReference(reference);

            // Calcul des montants
            double totalHT = orderToSave.getTotalAmountWithoutVAT();
            double totalTTC = orderToSave.getTotalAmountWithVAT();

            // Insertion de la commande
            String insertOrder = "INSERT INTO orders(reference, creation_datetime, total_ht, total_ttc) VALUES (?, ?, ?, ?) RETURNING id";
            try (PreparedStatement stmt = conn.prepareStatement(insertOrder)) {
                stmt.setString(1, orderToSave.getReference());
                stmt.setTimestamp(2, Timestamp.from(orderToSave.getCreationDatetime()));
                stmt.setDouble(3, totalHT);
                stmt.setDouble(4, totalTTC);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        orderToSave.setId(rs.getInt("id"));
                    }
                }
            }

            // Insertion des plats commandés
            String insertDishOrder = "INSERT INTO dish_order(id_order, id_dish, quantity) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertDishOrder)) {
                for (DishOrder dishOrder : orderToSave.getDishOrders()) {
                    stmt.setInt(1, orderToSave.getId());
                    stmt.setInt(2, dishOrder.getDish().getId());
                    stmt.setInt(3, dishOrder.getQuantity());
                    stmt.executeUpdate();
                }
            }

            conn.commit();
        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }
    return orderToSave;
}
public Order findOrderByReference(String reference) throws SQLException {
    Order order = null;
    try (Connection conn = DBConnection.getDBConnection()) {
        String query = "SELECT id, reference, creation_datetime, total_ht, total_ttc FROM orders WHERE reference = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, reference);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int orderId = rs.getInt("id");
                    order = new Order(
                        orderId,
                        rs.getString("reference"),
                        rs.getTimestamp("creation_datetime").toInstant(),
                        rs.getDouble("total_ht"),
                        rs.getDouble("total_ttc"),
                        findDishOrdersByOrderId(orderId, conn)
                    );
                } else {
                    throw new RuntimeException("Commande introuvable avec référence : " + reference);
                }
            }
        }
    }
    return order;
}

private List<DishOrder> findDishOrdersByOrderId(int orderId, Connection conn) throws SQLException {
    List<DishOrder> dishOrders = new ArrayList<>();
    String query = "SELECT id, id_dish, quantity FROM dish_order WHERE id_order = ?";
    try (PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setInt(1, orderId);
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Dish dish = findDishById(rs.getInt("id_dish")); // réutilise ta méthode existante
                DishOrder dishOrder = new DishOrder(
                    rs.getInt("id"),
                    dish,
                    rs.getInt("quantity")
                );
                dishOrders.add(dishOrder);
            }
        }
    }
    return dishOrders;
}

public Sale createSaleFrom(Order order) throws SQLException {
    if (order.getPaymentStatus() != PaymentStatusEnum.PAID) {
        throw new RuntimeException("Impossible de créer une vente : la commande n’est pas payée.");
    }

    if (order.getSale() != null) {
        throw new RuntimeException("Cette commande est déjà associée à une vente.");
    }

    try (Connection conn = DBConnection.getDBConnection()) {
        conn.setAutoCommit(false);
        try {
            String insertSale = "INSERT INTO sale(creation_datetime) VALUES (?) RETURNING id";
            int saleId;
            try (PreparedStatement stmt = conn.prepareStatement(insertSale)) {
                stmt.setTimestamp(1, Timestamp.from(Instant.now()));
                try (ResultSet rs = stmt.executeQuery()) {
                    rs.next();
                    saleId = rs.getInt("id");
                }
            }

            String updateOrder = "UPDATE orders SET id_sale = ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateOrder)) {
                stmt.setInt(1, saleId);
                stmt.setInt(2, order.getId());
                stmt.executeUpdate();
            }

            conn.commit();
            return new Sale(saleId, Instant.now());
        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }
}


}