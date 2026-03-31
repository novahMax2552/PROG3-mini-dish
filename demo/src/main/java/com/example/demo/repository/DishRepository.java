package com.example.demo.repository;

import com.example.demo.entity.*;
import 

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public class DishRepository {
    private final JdbcTemplate jdbcTemplate;

    public DishRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Dish findById(Long id) {
        String sql = "SELECT * FROM dish WHERE id=?";
        Dish dish = jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, rowNum) -> {
            Dish d = new Dish(rowNum, sql, null);
            d.setId(rs.getLong("id"));
            d.setName(rs.getString("name"));
            d.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));
            return d;
        });

        String sqlIng = "SELECT i.* FROM ingredient i JOIN dish_ingredient di ON i.id=di.ingredient_id WHERE di.dish_id=?";
        List<Ingredients> ingredients = jdbcTemplate.query(sqlIng, new Object[]{id}, (rs, rowNum) -> {
            Ingredient ing = new Ingredient();
            ing.setId(rs.getLong("id"));
            ing.setName(rs.getString("name"));
            ing.setPrice(rs.getDouble("price"));
            ing.setCategory(IngredientCategoryEnum.valueOf(rs.getString("category")));
            return ing;
        });
        dish.setIngredients(ingredients);

        return dish;
    }

    public void save(Dish dish) {
        if (dish.getId() == null) {
            jdbcTemplate.update("INSERT INTO dish(name, dish_type) VALUES (?, ?)",
                    dish.getName(), dish.getDishType().name());
        } else {
            jdbcTemplate.update("UPDATE dish SET name=?, dish_type=? WHERE id=?",
                    dish.getName(), dish.getDishType().name(), dish.getId());
            jdbcTemplate.update("DELETE FROM dish_ingredient WHERE dish_id=?", dish.getId());
        }
        for (Ingredients ing : dish.getIngredients()) {
            jdbcTemplate.update("INSERT INTO dish_ingredient(dish_id, ingredient_id) VALUES (?, ?)",
                    dish.getId(), ing.getId());
        }
    }

    public List<Dish> findAll() {
    String sql = "SELECT * FROM dish";
    List<Dish> dishes = jdbcTemplate.query(sql, (rs, rowNum) -> {
        Dish d = new Dish();
        d.setId(rs.getLong("id"));
        d.setName(rs.getString("name"));
        d.setSalePrice(rs.getDouble("sale_price")); // colonne à ajouter dans schema.sql
        return d;
    });

    // Charger les ingrédients pour chaque plat
    for (Dish dish : dishes) {
        String sqlIng = "SELECT i.* FROM ingredient i JOIN dish_ingredient di ON i.id=di.ingredient_id WHERE di.dish_id=?";
        List<Ingredients> ingredients = jdbcTemplate.query(sqlIng, new Object[]{dish.getId()}, (rs, rowNum) -> {
            Ingredients ing = new Ingredients();
            ing.setId(rs.getLong("id"));
            ing.setName(rs.getString("name"));
            ing.setPrice(rs.getDouble("price"));
            ing.setCategory(IngredientCategoryEnum.valueOf(rs.getString("category")));
            return ing;
        });
        dish.setIngredients(ingredients);
    }

    return dishes;
}

public boolean updateIngredients(Long dishId, List<Ingredients> ingredients) {
    // Vérifier si le plat existe
    String checkSql = "SELECT COUNT(*) FROM dish WHERE id=?";
    Integer count = jdbcTemplate.queryForObject(checkSql, new Object[]{dishId}, Integer.class);
    if (count == null || count == 0) {
        return false;
    }

    // Supprimer les anciennes associations
    jdbcTemplate.update("DELETE FROM dish_ingredient WHERE dish_id=?", dishId);

    // Associer uniquement les ingrédients existants
    for (Ingredients ing : ingredients) {
        String checkIngSql = "SELECT COUNT(*) FROM ingredient WHERE id=?";
        Integer ingCount = jdbcTemplate.queryForObject(checkIngSql, new Object[]{ing.getId()}, Integer.class);
        if (ingCount != null && ingCount > 0) {
            jdbcTemplate.update("INSERT INTO dish_ingredient(dish_id, ingredient_id) VALUES (?, ?)", dishId, ing.getId());
        }
    }
    return true;
}

}
