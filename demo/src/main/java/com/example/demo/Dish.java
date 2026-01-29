package com.example.demo;

import java.util.List;

public class Dish {
    private Integer id;
    private String name;
    private DishType dishType;
    private Double sellingPrice;
    private List<Ingredients> ingredients;

    public Dish() {}

    public Dish(Integer id, String name, DishType dishType) {
        this.id = id;
        this.name = name;
        this.dishType = dishType;
    }

    public Dish(Integer id, String name, DishType dishType, List<Ingredients> ingredients) {
        this.id = id;
        this.name = name;
        this.dishType = dishType;
        this.ingredients = ingredients;
    }

    public Dish(Integer id, String name, DishType dishType, Double sellingPrice) {
        this.id = id;
        this.name = name;
        this.dishType = dishType;
        this.sellingPrice = sellingPrice;
    }
    public Dish(Integer id, String name, DishType dishType, List<Ingredients> ingredients, double sellingPrice) {
        this.id = id;
        this.name = name;
        this.dishType = dishType;
        this.ingredients = ingredients;
        this.sellingPrice = sellingPrice;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { 
        return name; 
    }

    public void setName(String name) { 
        this.name = name; 
    }

    public DishType getDishType() { 
        return dishType; 
    }

    public void setDishType(DishType dishType) { 
        this.dishType = dishType; 
    }

    public List<Ingredients> getIngredients() { 
        return ingredients; 
    }

    public void setIngredients(List<Ingredients> ingredients) { 
        this.ingredients = ingredients; 
    }

    public Double getDishCost() {
    if (ingredients == null || ingredients.isEmpty()) {
        return 0.0;
    }
    double total = 0.0;
    for (Ingredients ing : ingredients) {
        if (ing.getRequiredQuantity() == null) {
            throw new RuntimeException("Quantité nécessaire inconnue pour l'ingrédient : " + ing.getName());
        }
        total += ing.getPrice() * ing.getRequiredQuantity();
    }
    return total;
}
    public Double getSellingPrice() {
        return sellingPrice;
    }
    public void setSellingPrice(Double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public Double getGrossMargin() {
    if (sellingPrice == null) {
        throw new RuntimeException("Prix de vente manquant pour le plat : " + name);
    }
    return sellingPrice - getDishCost();
}



    @Override
    public String toString() {
        return "Dish{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", dishType=" + dishType +
                ", ingredients=" + ingredients +
                '}';
    }
}
