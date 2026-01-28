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
            PreparedStatement stmt = conn.prepareStatement("SELECT id, name, dish_type FROM Dish WHERE id=?");
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                dish = new Dish(rs.getInt("id"), rs.getString("name"),
                        DishType.valueOf(rs.getString("dish_type")));
            }

            if (dish != null) {
                PreparedStatement stmtIng = conn.prepareStatement("SELECT * FROM Ingredient WHERE id_dish=?");
                stmtIng.setInt(1, id);
                ResultSet rsIng = stmtIng.executeQuery();
                List<Ingredients> ingrédients = new ArrayList<>();
                while (rsIng.next()) {
                    ingrédients.add(new Ingredients(
                            rsIng.getInt("id"),
                            rsIng.getString("name"),
                            rsIng.getDouble("price"),
                            CategoryEnum.valueOf(rsIng.getString("category")),
                            rsIng.getInt("id_dish")
                    ));
                }
                dish.setIngredients(ingrédients);
            }
        }
        return dish;
    }

    public List<Ingredients> findIngredients(int page, int size) throws SQLException {
        List<Ingredients> ingrédients = new ArrayList<>();
        try (Connection conn = DBConnection.getDBConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT * FROM Ingredient ORDER BY id LIMIT ? OFFSET ?");
            stmt.setInt(1, size);
            stmt.setInt(2, (page - 1) * size);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ingrédients.add(new Ingredients(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        CategoryEnum.valueOf(rs.getString("category")),
                        rs.getInt("id_dish")
                ));
            }
        }
        return ingrédients;
    }

    public List<Ingredients> createIngredients(List<Ingredients> newIngredients) {
        List<Ingredients> created = new ArrayList<>();
        try (Connection conn = DBConnection.getDBConnection()) {
            conn.setAutoCommit(false);
            try {
                for (Ingredients ing : newIngredients) {
                    PreparedStatement check = conn.prepareStatement("SELECT id FROM Ingredient WHERE name=?");
                    check.setString(1, ing.getName());
                    ResultSet rs = check.executeQuery();
                    if (rs.next()) {
                        conn.rollback();
                        throw new RuntimeException("Ingrédient déjà existant : " + ing.getName());
                    }
                }
                for (Ingredients ing : newIngredients) {
                    String insertQuery = "INSERT INTO ingredient (name, price, category) VALUES (?, ?, ?::category_enum) RETURNING id";
                    PreparedStatement insert = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
                    insert.setString(1, ing.getName());
                    insert.setDouble(2, ing.getPrice());
                    insert.setString(3, ing.getCategory().name());
                    insert.executeUpdate();
                    ResultSet keys = insert.getGeneratedKeys();
                    if (keys.next()) ing.setId(keys.getInt(1));
                    created.add(ing);
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
            if (dish.getId() == null) {
                PreparedStatement insert = conn.prepareStatement(
                    "INSERT INTO Dish(name, dish_type) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
                insert.setString(1, dish.getName());
                insert.setString(2, dish.getDishType().name());
                insert.executeUpdate();
                ResultSet keys = insert.getGeneratedKeys();
                if (keys.next()) dish.setId(keys.getInt(1));
            } else {
                PreparedStatement update = conn.prepareStatement(
                    "UPDATE Dish SET name=?, dish_type=? WHERE id=?");
                update.setString(1, dish.getName());
                update.setString(2, dish.getDishType().name());
                update.setInt(3, dish.getId());
                update.executeUpdate();
            }

            PreparedStatement clear = conn.prepareStatement("UPDATE Ingredient SET id_dish=NULL WHERE id_dish=?");
            clear.setInt(1, dish.getId());
            clear.executeUpdate();

            for (Ingredients ing : dish.getIngredients()) {
                PreparedStatement link = conn.prepareStatement("UPDATE Ingredient SET id_dish=? WHERE name=?");
                link.setInt(1, dish.getId());
                link.setString(2, ing.getName());
                link.executeUpdate();
            }
        }
        return dish;
    }

    public List<Dish> findDishsByIngredientName(String ingredientName) throws SQLException {
        List<Dish> dishes = new ArrayList<>();
        try (Connection conn = DBConnection.getDBConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT DISTINCT d.id, d.name, d.dish_type " +
                "FROM Dish d JOIN Ingredient i ON d.id=i.id_dish " +
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
        List<Ingredients> ingrédients = new ArrayList<>();
        StringBuilder query = new StringBuilder(
            "SELECT i.id, i.name, i.price, i.category, i.id_dish " +
            "FROM Ingredient i LEFT JOIN Dish d ON i.id_dish = d.id WHERE 1=1 ");
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
                ingrédients.add(new Ingredients(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    CategoryEnum.valueOf(rs.getString("category")),
                    rs.getInt("id_dish")
                ));
            }
        }
        return ingrédients;
    }
}
