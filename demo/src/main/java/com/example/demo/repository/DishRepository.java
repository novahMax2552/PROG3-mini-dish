package com.example.demo.repository;

import com.example.demo.entity.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DishRepository {
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;

    public Dish findById(Long id) {
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM dish WHERE id=?");
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Dish dish = new Dish();
                dish.setId(rs.getLong("id"));
                dish.setName(rs.getString("name"));
                dish.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));
                dish.setSellingPrice(rs.getDouble("sale_price"));

                // Charger les ingrédients associés
                dish.setIngredients(loadIngredients(conn, id));
                return dish;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private List<Ingredients> loadIngredients(Connection conn, Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'loadIngredients'");
    }

    private List<Ingredients> loadIngredients(Connection conn, Integer id) throws SQLException {
        List<Ingredients> ingredients = new ArrayList<>();
        PreparedStatement ps = conn.prepareStatement(
            "SELECT i.* FROM ingredient i JOIN dish_ingredient di ON i.id=di.ingredient_id WHERE di.dish_id=?");
        ps.setLong(1, id);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Ingredients ing = new Ingredients(null, password, null, null);
            ing.setId(rs.getLong("id"));
            ing.setName(rs.getString("name"));
            ing.setPrice(rs.getDouble("price"));
            ing.setCategory(IngredientCategoryEnum.valueOf(rs.getString("category")));
            ingredients.add(ing);
        }
        return ingredients;
    }

    public List<Dish> findAll() {
        List<Dish> dishes = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM dish");
            while (rs.next()) {
                Dish dish = new Dish();
                dish.setId(rs.getLong("id"));
                dish.setName(rs.getString("name"));
                dish.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));
                dish.setSellingPrice(rs.getDouble("sale_price"));
                dish.setIngredients(loadIngredients(conn, dish.getId()));
                dishes.add(dish);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return dishes;
    }

    public boolean updateIngredients(Long dishId, List<Ingredients> ingredients) {
    try (Connection conn = DriverManager.getConnection(url, username, password)) {
        // Vérifier si le plat existe
        PreparedStatement check = conn.prepareStatement("SELECT COUNT(*) FROM dish WHERE id=?");
        check.setLong(1, dishId);
        ResultSet rs = check.executeQuery();
        if (rs.next() && rs.getInt(1) == 0) {
            return false; // plat non trouvé
        }

        // Supprimer les anciennes associations
        PreparedStatement delete = conn.prepareStatement("DELETE FROM dish_ingredient WHERE dish_id=?");
        delete.setLong(1, dishId);
        delete.executeUpdate();

        // Associer uniquement les ingrédients existants
        for (Ingredients ing : ingredients) {
            PreparedStatement checkIng = conn.prepareStatement("SELECT COUNT(*) FROM ingredient WHERE id=?");
            checkIng.setLong(1, ing.getId());
            ResultSet rsIng = checkIng.executeQuery();
            if (rsIng.next() && rsIng.getInt(1) > 0) {
                PreparedStatement insert = conn.prepareStatement(
                        "INSERT INTO dish_ingredient(dish_id, ingredient_id) VALUES (?, ?)");
                insert.setLong(1, dishId);
                insert.setLong(2, ing.getId());
                insert.executeUpdate();
            }
        }
        return true;
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
}

}
