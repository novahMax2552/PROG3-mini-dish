package com.example.demo;

import java.util.List;

public class Dish {
    private Integer id;
    private String name;
    private DishType dishType;
    private Double sellingPrice;
    private List<Ingredients> ingredients;

    public Dish(Integer id, String name, DishType dishType, Double sellingPrice) {
        this.id = id;
        this.name = name;
        this.dishType = dishType;
        this.sellingPrice = sellingPrice;
    }

    public Dish(Integer id, String name, DishType dishType) {
        this(id, name, dishType, null);
    }

    // Getters & setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public DishType getDishType() { return dishType; }
    public void setDishType(DishType dishType) { this.dishType = dishType; }

    public Double getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(Double sellingPrice) { this.sellingPrice = sellingPrice; }

    public List<Ingredients> getIngredients() { return ingredients; }
    public void setIngredients(List<Ingredients> ingredients) { this.ingredients = ingredients; }

    @Override
    public String toString() {
        return "Dish{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", dishType=" + dishType +
                ", sellingPrice=" + sellingPrice +
                ", ingredients=" + ingredients +
                '}';
    }
}
