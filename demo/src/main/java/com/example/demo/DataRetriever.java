package com.example.demo;

import java.sql.*;
import java.util.*;

public class DataRetriever {

    public DataRetriever(DBConnection dbConn) {
        //TODO Auto-generated constructor stub
    }

    public Dish findDishById(Integer id) throws SQLException {
        Dish dish = null;
        try (Connection conn = DBConnection.getDBConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT id, name, dish_type FROM dish WHERE id=?");
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                dish = new Dish(rs.getInt("id"), rs.getString("name"),
                        DishType.valueOf(rs.getString("dish_type")));
            }

            if (dish != null) {
                PreparedStatement stmtIng = conn.prepareStatement("SELECT * FROM ingredient WHERE id_dish=?");
                stmtIng.setInt(1, id);
                ResultSet rsIng = stmtIng.executeQuery();
                List<Ingredients> ingredients = new ArrayList<>();
                while (rsIng.next()) {
                    ingredients.add(new Ingredients(
                            rsIng.getInt("id"),
                            rsIng.getString("name"),
                            rsIng.getDouble("price"),
                            CategoryEnum.valueOf(rsIng.getString("category")),
                            rsIng.getInt("id_dish")
                    ));
                }
                dish.setIngredients(ingredients);
            }
        }
        return dish;
    }

    public List<Ingredients> findIngredients(int page, int size) throws SQLException {
        List<Ingredients> ingredients = new ArrayList<>();
        try (Connection conn = DBConnection.getDBConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT * FROM ingredient ORDER BY id LIMIT ? OFFSET ?");
            stmt.setInt(1, size);
            stmt.setInt(2, (page - 1) * size);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ingredients.add(new Ingredients(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        CategoryEnum.valueOf(rs.getString("category")),
                        rs.getInt("id_dish")
                ));
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


    public Dish saveDish(Dish dish) throws SQLException {
    try (Connection conn = DBConnection.getDBConnection()) {
        conn.setAutoCommit(false);
        try {
            if (dish.getId() == null) {
                String insertQuery = "INSERT INTO dish(name, dish_type) VALUES (?, ?::dish_type_enum) RETURNING id";
                try (PreparedStatement insert = conn.prepareStatement(insertQuery)) {
                    insert.setString(1, dish.getName());
                    insert.setObject(2, dish.getDishType().name(), java.sql.Types.OTHER);

                    try (ResultSet rs = insert.executeQuery()) {
                        if (rs.next()) {
                            dish.setId(rs.getInt("id"));
                        }
                    }
                }
            } else {
                String updateQuery = "UPDATE dish SET name=?, dish_type=?::dish_type_enum WHERE id=?";
                try (PreparedStatement update = conn.prepareStatement(updateQuery)) {
                    update.setString(1, dish.getName());
                    update.setObject(2, dish.getDishType().name(), java.sql.Types.OTHER);
                    update.setInt(3, dish.getId());
                    update.executeUpdate();
                }
            }

            String clearQuery = "UPDATE ingredient SET id_dish=NULL WHERE id_dish=?";
            try (PreparedStatement clear = conn.prepareStatement(clearQuery)) {
                clear.setInt(1, dish.getId());
                clear.executeUpdate();
            }

            String linkQuery = "UPDATE ingredient SET id_dish=? WHERE name=? RETURNING id, id_dish";
            try (PreparedStatement link = conn.prepareStatement(linkQuery)) {
                for (Ingredients ing : dish.getIngredients()) {
                    link.setInt(1, dish.getId());
                    link.setString(2, ing.getName());

                    try (ResultSet rs = link.executeQuery()) {
                        if (rs.next()) {
                            ing.setId(rs.getInt("id"));
                            ing.setDishId(rs.getInt("id_dish"));
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
    }
    return dish;
}


    public List<Dish> findDishsByIngredientName(String ingredientName) throws SQLException {
        List<Dish> dishes = new ArrayList<>();
        try (Connection conn = DBConnection.getDBConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT DISTINCT d.id, d.name, d.dish_type " +
                "FROM dish d JOIN ingredient i ON d.id=i.id_dish " +
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
            "SELECT i.id, i.name, i.price, i.category, i.id_dish " +
            "FROM ingredient i LEFT JOIN dish d ON i.id_dish = d.id WHERE 1=1 ");
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
                ingredients.add(new Ingredients(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    CategoryEnum.valueOf(rs.getString("category")),
                    rs.getInt("id_dish")
                ));
            }
        }
        return ingredients;
    }
}
