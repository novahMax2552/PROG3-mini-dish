package com.example.demo.repository;

import com.example.demo.entity.*;
import com.example.demo.entity.Ingredients;
import com.example.demo.service.IngredientService;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class IngredientRepository {
    private final JdbcTemplate jdbcTemplate;

    public IngredientRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Ingredients> findAll(int page, int size) {
        String sql = "SELECT * FROM ingredient ORDER BY id LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, new Object[]{size, (page - 1) * size}, (rs, rowNum) -> {
            Ingredient ing = new Ingredient();
            ing.setId(rs.getLong("id"));
            ing.setName(rs.getString("name"));
            ing.setPrice(rs.getDouble("price"));
            ing.setCategory(IngredientCategoryEnum.valueOf(rs.getString("category")));
            return ing;
        });
    }

    public void saveAll(List<Ingredients> ingredients) {
        for (Ingredients ing : ingredients) {
            jdbcTemplate.update("INSERT INTO ingredient(name, price, category) VALUES (?, ?, ?)",
                    ing.getName(), ing.getPrice(), ing.getCategory().name());
        }
    }

	public Ingredients findById(Long id) {
    String sql = "SELECT * FROM ingredient WHERE id=?";
    List<Ingredients> list = jdbcTemplate.query(sql, new Object[]{id}, (rs, rowNum) -> {
        Ingredient ing = new Ingredient();
        ing.setId(rs.getLong("id"));
        ing.setName(rs.getString("name"));
        ing.setPrice(rs.getDouble("price"));
        ing.setCategory(IngredientCategoryEnum.valueOf(rs.getString("category")));
        return ing;
    });
    return list.isEmpty() ? null : list.get(0);
    }

    public Double findStockValue(Long id, String at, String unit) {
        String sql = "SELECT value FROM stock WHERE ingredient_id=? AND date=? AND unit=?";
        List<Double> values = jdbcTemplate.query(sql, new Object[]{id, at, unit}, (rs, rowNum) -> rs.getDouble("value"));
        return values.isEmpty() ? null : values.get(0);
    }

}
