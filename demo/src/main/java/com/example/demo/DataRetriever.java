package com.example.demo;

import java.sql.*;
import java.util.*;

public class DataRetriever {

    public DataRetriever(DBConnection dbConn) {
        //TODO Auto-generated constructor stub
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
            "SELECT i.id, i.name, i.price, i.category, di.quantity_required " +
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
            ing.setRequiredQuantity(rs.getObject("quantity_required") != null ? rs.getDouble("quantity_required") : null);
            ingredients.add(ing);
        }
    }
    return ingredients;
}


    public List<Ingredients> findIngredients(int page, int size) throws SQLException {
    List<Ingredients> ingredients = new ArrayList<>();
    try (Connection conn = DBConnection.getDBConnection()) {
        PreparedStatement stmt = conn.prepareStatement(
            "SELECT i.id, i.name, i.price, i.category, di.id_dish, di.quantity_required " +
            "FROM ingredient i " +
            "LEFT JOIN dish_ingredient di ON i.id = di.id_ingredient " +
            "ORDER BY i.id LIMIT ? OFFSET ?");
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
            ing.setDishId(rs.getObject("id_dish") != null ? rs.getInt("id_dish") : null);
            ing.setRequiredQuantity(rs.getObject("quantity_required") != null ? rs.getDouble("quantity_required") : null);
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
                String insertQuery = "INSERT INTO ingredient (name, price, category, required_quantity) VALUES (?, ?, ?::category_enum, ?) RETURNING id";

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
                        if (ing.getRequiredQuantity() != null) {
                            insertStmt.setDouble(4, ing.getRequiredQuantity());
                        } else {
                            insertStmt.setNull(4, java.sql.Types.DOUBLE);
                        }

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

                    // Vérifier si l’ingrédient existe déjà par son nom
                    if (ing.getId() == null) {
                        String checkIngredient = "SELECT id FROM ingredient WHERE name = ?";
                        try (PreparedStatement checkIng = conn.prepareStatement(checkIngredient)) {
                            checkIng.setString(1, ing.getName());
                            try (ResultSet rsCheck = checkIng.executeQuery()) {
                                if (rsCheck.next()) {
                                    // L’ingrédient existe déjà → récupérer son ID
                                    ing.setId(rsCheck.getInt("id"));
                                } else {
                                    // L’ingrédient n’existe pas → insérer
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
                            }
                        }
                    }

                    // Créer la liaison dans dish_ingredient
                    link.setInt(1, dish.getId());
                    link.setInt(2, ing.getId());
                    if (ing.getRequiredQuantity() != null) {
                        link.setDouble(3, ing.getRequiredQuantity());
                    } else {
                        link.setNull(3, java.sql.Types.NUMERIC);
                    }
                    // ⚠️ Ici, adapte selon ton modèle : "KG", "PCS", "L"
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
        "SELECT i.id, i.name, i.price, i.category, di.id_dish, di.quantity_required " +
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
            ing.setDishId(rs.getObject("id_dish") != null ? rs.getInt("id_dish") : null);
            ing.setRequiredQuantity(rs.getObject("quantity_required") != null ? rs.getDouble("quantity_required") : null);
            ingredients.add(ing);
        }
    }
    return ingredients;
    }
    public Ingredients saveIngredient(Ingredients toSave) throws SQLException {
    try (Connection conn = DBConnection.getDBConnection()) {
        conn.setAutoCommit(false);
        try {
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

            String insertMovement = "INSERT INTO stock_movement(id, id_ingredient, quantity, type, unit, creation_datetime) " +
                                    "VALUES (?, ?, ?, ?::movement_type, ?::unit_type, ?) ON CONFLICT DO NOTHING";
            try (PreparedStatement movementStmt = conn.prepareStatement(insertMovement)) {
                for (StockMovement movement : toSave.getStockMovements()) {
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

}