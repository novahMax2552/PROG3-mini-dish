package com.example.demo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {

    private DBConnection dbConnection;

    public DataRetriever(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public List<Dish> getAllDishes() {
        List<Dish> dishes = new ArrayList<>();

        String query = "SELECT d.id AS dish_id, d.name AS dish_name, d.type AS dish_type, " +
                       "i.id AS ingredient_id, i.name AS ingredient_name, i.price AS ingredient_price, " +
                       "i.category AS ingredient_category, di.required_quantity " +
                       "FROM dish d " +
                       "LEFT JOIN dish_ingredients di ON d.id = di.dish_id " +
                       "LEFT JOIN ingredients i ON di.ingredient_id = i.id";

        try (Connection conn = dbConnection.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            java.util.Map<Integer, Dish> dishMap = new java.util.HashMap<>();

            while (rs.next()) {
                int dishId = rs.getInt("dish_id");
                String dishName = rs.getString("dish_name");
                DishTypeEnum dishType = DishTypeEnum.valueOf(rs.getString("dish_type"));

                Dish dish = dishMap.get(dishId);
                if (dish == null) {
                    dish = new Dish(dishId, dishName, dishType, new ArrayList<>());
                    dishMap.put(dishId, dish);
                }

                // Si l’ingrédient existe
                int ingredientId = rs.getInt("ingredient_id");
                if (ingredientId > 0) {
                    String ingredientName = rs.getString("ingredient_name");
                    Double ingredientPrice = rs.getDouble("ingredient_price");
                    CategoryEnum category = CategoryEnum.valueOf(rs.getString("ingredient_category"));
                    Double requiredQuantity = rs.getDouble("required_quantity");

                    Ingredients ingredient = new Ingredients(
                            ingredientId,
                            ingredientName,
                            ingredientPrice,
                            category,
                            dish,
                            requiredQuantity
                    );

                    dish.getIngredients().add(ingredient);
                }
            }

            dishes.addAll(dishMap.values());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return dishes;
    }
}
