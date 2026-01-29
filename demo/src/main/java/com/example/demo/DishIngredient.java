package com.example.demo;

public class DishIngredient {
    private Integer id;
    private Integer dishId;
    private Ingredients ingredient;
    private Double quantityRequired;
    private UnitType unit;

    public DishIngredient() {}
    public DishIngredient(Integer id, Integer dishId, Ingredients ingredient, Double quantityRequired, UnitType unit) {
        this.id = id;
        this.dishId = dishId;
        this.ingredient = ingredient;
        this.quantityRequired = quantityRequired;
        this.unit = unit;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getDishId() {
        return dishId;
    }
    public void setDishId(Integer dishId) {
        this.dishId = dishId;
    }
    public Ingredients getIngredient() {
        return ingredient;
    }
    public void setIngredient(Ingredients ingredient) {
        this.ingredient = ingredient;
    }
    public Double getQuantityRequired() {
        return quantityRequired;
    }
    public void setQuantityRequired(Double quantityRequired) {
        this.quantityRequired = quantityRequired;
    }
    public UnitType getUnit() {
        return unit;
    }
    public void setUnit(UnitType unit) {
        this.unit = unit;
    }
}

