package com.example.demo;

import java.sql.*;
import java.util.*;

public class DataRetriever {

    private DBConnection dbConnection;

    public DataRetriever(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    // a) Récupérer un plat par ID avec ses ingrédients
    public Dish findDishById(Integer id) {
        Dish dish = null;
        String dishQuery = "SELECT id, name, type FROM dish WHERE id = ?";
        String ingQuery = "SELECT i.id, i.name, i.price, i.category, di.required_quantity " +
                          "FROM ingredients i " +
                          "JOIN dish_ingredients di ON i.id = di.ingredient_id " +
                          "WHERE di.dish_id = ?";

        try (Connection conn = dbConnection.getDBConnection();
             PreparedStatement dishStmt = conn.prepareStatement(dishQuery);
             PreparedStatement ingStmt = conn.prepareStatement(ingQuery)) {

            dishStmt.setInt(1, id);
            ResultSet dishRs = dishStmt.executeQuery();

            if (dishRs.next()) {
                dish = new Dish(
                        dishRs.getInt("id"),
                        dishRs.getString("name"),
                        DishTypeEnum.valueOf(dishRs.getString("type")),
                        new ArrayList<>()
                );

                ingStmt.setInt(1, id);
                ResultSet ingRs = ingStmt.executeQuery();
                while (ingRs.next()) {
                    Ingredients ing = new Ingredients(
                            ingRs.getInt("id"),
                            ingRs.getString("name"),
                            ingRs.getDouble("price"),
                            CategoryEnum.valueOf(ingRs.getString("category")),
                            dish,
                            ingRs.getDouble("required_quantity")
                    );
                    dish.getIngredients().add(ing);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dish;
    }

    // b) Récupérer les ingrédients avec pagination
    public List<Ingredients> findIngredients(int page, int size) {
        List<Ingredients> ingredients = new ArrayList<>();
        String query = "SELECT id, name, price, category FROM ingredients LIMIT ? OFFSET ?";

        try (Connection conn = dbConnection.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, size);
            stmt.setInt(2, page * size);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ingredients.add(new Ingredients(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        CategoryEnum.valueOf(rs.getString("category")),
                        null,
                        null
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ingredients;
    }

    // c) Créer des ingrédients avec atomicité
    public List<Ingredients> createIngredients(List<Ingredients> newIngredients) {
        List<Ingredients> created = new ArrayList<>();
        String checkQuery = "SELECT COUNT(*) FROM ingredients WHERE name = ?";
        String insertQuery = "INSERT INTO ingredients (name, price, category) VALUES (?, ?, ?) RETURNING id";

        try (Connection conn = dbConnection.getDBConnection()) {
            conn.setAutoCommit(false); // début transaction

            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
                 PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {

                for (Ingredients ing : newIngredients) {
                    // Vérifier existence
                    checkStmt.setString(1, ing.getName());
                    ResultSet rs = checkStmt.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0) {
                        conn.rollback();
                        throw new RuntimeException("Ingredient déjà existant: " + ing.getName());
                    }

                    // Insérer
                    insertStmt.setString(1, ing.getName());
                    insertStmt.setDouble(2, ing.getPrice());
                    insertStmt.setString(3, ing.getCategory().name());
                    ResultSet insertRs = insertStmt.executeQuery();
                    if (insertRs.next()) {
                        created.add(new Ingredients(
                                insertRs.getInt("id"),
                                ing.getName(),
                                ing.getPrice(),
                                ing.getCategory(),
                                null,
                                null
                        ));
                    }
                }

                conn.commit(); // valider transaction

            } catch (Exception e) {
                conn.rollback(); // annuler tout si erreur
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return created;
    }

    // d) Sauvegarder un plat (insert ou update + associer/dissocier ingrédients)
    public Dish saveDish(Dish dishToSave) {
        String checkQuery = "SELECT COUNT(*) FROM dish WHERE id = ?";
        String insertQuery = "INSERT INTO dish (name, type) VALUES (?, ?) RETURNING id";
        String updateQuery = "UPDATE dish SET name = ?, type = ? WHERE id = ?";
        String deleteAssoc = "DELETE FROM dish_ingredients WHERE dish_id = ?";
        String insertAssoc = "INSERT INTO dish_ingredients (dish_id, ingredient_id, required_quantity) VALUES (?, ?, ?)";

        Dish savedDish = null;

        try (Connection conn = dbConnection.getDBConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
                 PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
                 PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                 PreparedStatement deleteStmt = conn.prepareStatement(deleteAssoc);
                 PreparedStatement assocStmt = conn.prepareStatement(insertAssoc)) {

                int dishId = dishToSave.getId();

                // Vérifier existence
                checkStmt.setInt(1, dishId);
                ResultSet rs = checkStmt.executeQuery();
                boolean exists = rs.next() && rs.getInt(1) > 0;

                if (!exists) {
                    insertStmt.setString(1, dishToSave.getName());
                    insertStmt.setString(2, dishToSave.getDishType().name());
                    ResultSet insertRs = insertStmt.executeQuery();
                    if (insertRs.next()) {
                        dishId = insertRs.getInt("id");
                    }
                } else {
                    updateStmt.setString(1, dishToSave.getName());
                    updateStmt.setString(2, dishToSave.getDishType().name());
                    updateStmt.setInt(3, dishId);
                    updateStmt.executeUpdate();

                    // Supprimer anciennes associations
                    deleteStmt.setInt(1, dishId);
                    deleteStmt.executeUpdate();
                }

                // Associer les nouveaux ingrédients
                if (dishToSave.getIngredients() != null) {
                    for (Ingredients ing : dishToSave.getIngredients()) {
                        assocStmt.setInt(1, dishId);
                        assocStmt.setInt(2, ing.getId());
                        assocStmt.setDouble(3, ing.getRequiredQuantity() != null ? ing.getRequiredQuantity() : 1.0);
                        assocStmt.executeUpdate();
                    }
                }

                conn.commit();
                savedDish = new Dish(dishId, dishToSave.getName(), dishToSave.getDishType(), dishToSave.getIngredients());

            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return savedDish;
    }

    // e) Trouver plats par nom d’ingrédient
    public List<Dish> findDishsByIngredientName(String ingredientName) {
        List<Dish> dishes = new ArrayList<>();
        String query = "SELECT DISTINCT d.id, d.name, d.type " +
                       "FROM dish d " +
                       "JOIN dish_ingredients di ON d.id = di.dish_id " +
                       "JOIN ingredients i ON di.ingredient_id = i.id " +
                       "WHERE i.name ILIKE ?";

        try (Connection conn = dbConnection.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + ingredientName + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                dishes.add(new Dish(
                        rs.getInt("id"),
                        rs.getString("name"),
                        DishTypeEnum.valueOf(rs.getString("type")),
                        new ArrayList<>()
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dishes;
    }

    // f) Recherche ingrédients par critères avec pagination
    public List<Ingredients> findIngredientsByCriteria(String ingredientName, CategoryEnum category, String dishName, int page, int size) {
        List<Ingredients> ingredients = new ArrayList<>();
        StringBuilder query = new StringBuilder(
                "SELECT i.id, i.name, i.price, i.category " +
                "FROM ingredients i " +
                "LEFT JOIN dish_ingredients di ON i.id = di.ingredient_id " +
                "LEFT JOIN dish d ON di.dish_id = d.id WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        if (ingredientName != null && !ingredientName.isEmpty()) {
            query.append("AND i.name ILIKE ? ");
            params.add("%" + ingredientName + "%");
        }
        if (category != null) {
            query.append("AND i.category = ? ");
            params.add(category.name());
        }
        if (dishName != null && !dishName.isEmpty()) {
            query.append("AND d.name ILIKE ? ");
            params.add("%" + dishName + "%");
        }

        query.append("LIMIT ? OFFSET ?");
        params.add(size);
        params.add(page * size);

        try (Connection conn = dbConnection.getDBConnection();
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
                        null,
                        null
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ingredients;
    }
}
