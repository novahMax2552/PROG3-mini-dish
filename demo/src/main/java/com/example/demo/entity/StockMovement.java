package com.example.demo.entity;

import java.time.Instant;

public class StockMovement {
    private Long id;
    private Instant createdAt;
    private String unit;   // PCS, L, KG
    private Double value;
    private String type;   // ENTRY ou EXIT

    // Getter et Setter pour id
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    // Getter et Setter pour createdAt
    public Instant getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    // Getter et Setter pour unit
    public String getUnit() {
        return unit;
    }
    public void setUnit(String unit) {
        this.unit = unit;
    }

    // Getter et Setter pour value
    public Double getValue() {
        return value;
    }
    public void setValue(Double value) {
        this.value = value;
    }

    // Getter et Setter pour type
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
}
