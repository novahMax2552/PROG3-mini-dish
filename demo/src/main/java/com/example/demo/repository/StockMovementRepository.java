package com.example.demo.repository;

import com.example.demo.entity.StockMovement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Repository
public class StockMovementRepository {
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;

    public List<StockMovement> findByIngredientAndPeriod(Long ingredientId, Instant from, Instant to) {
        List<StockMovement> movements = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM stock_movement WHERE ingredient_id=? AND created_at BETWEEN ? AND ?");
            ps.setLong(1, ingredientId);
            ps.setTimestamp(2, Timestamp.from(from));
            ps.setTimestamp(3, Timestamp.from(to));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                StockMovement sm = new StockMovement();
                sm.setId(rs.getLong("id"));
                sm.setCreatedAt(rs.getTimestamp("created_at").toInstant());
                sm.setUnit(rs.getString("unit"));
                sm.setValue(rs.getDouble("value"));
                sm.setType(rs.getString("type"));
                movements.add(sm);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return movements;
    }

    public List<StockMovement> saveAll(Long ingredientId, List<StockMovement> newMovements) {
        List<StockMovement> saved = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            for (StockMovement sm : newMovements) {
                PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO stock_movement(ingredient_id, created_at, unit, value, type) VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, ingredientId);
                ps.setTimestamp(2, Timestamp.from(Instant.now())); // date de création
                ps.setString(3, sm.getUnit());
                ps.setDouble(4, sm.getValue());
                ps.setString(5, sm.getType());
                ps.executeUpdate();

                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    sm.setId(keys.getLong(1));
                    sm.setCreatedAt(Instant.now());
                    saved.add(sm);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return saved;
    }
}

