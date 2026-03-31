package com.example.demo.entity;

import java.util.List;

import com.example.demo.DishType;

public class Dish {
    private Integer id;
    private String name;
    private DishType dishType;
    private Double sellingPrice;   // ⚡ on garde sellingPrice
    private List<Ingredients> ingredients;

    // Constructeur complet
    public Dish(Integer id, String name, DishType dishType, List<Ingredients> ingredients, Double sellingPrice) {
        this.id = id;
        this.name = name;
        this.dishType = dishType;
        this.ingredients = ingredients;
        this.sellingPrice = sellingPrice;
    }

    // Constructeur avec ingrédients mais sans prix
    public Dish(Integer id, String name, DishType dishType, List<Ingredients> ingredients) {
        this(id, name, dishType, ingredients, null);
    }

    // Constructeur avec prix mais sans ingrédients
    public Dish(Integer id, String name, DishType dishType, Double sellingPrice) {
        this(id, name, dishType, null, sellingPrice);
    }

    // Constructeur minimal
    public Dish(Integer id, String name, DishType dishType) {
        this(id, name, dishType, null, null);
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

    // Calcul du coût du plat (simplifié : somme des prix des ingrédients)
    public double getDishCost() {
        double total = 0.0;
        if (ingredients != null) {
            for (Ingredients ing : ingredients) {
                total += ing.getPrice();
            }
        }
        return total;
    }

    // Calcul de la marge brute
    public double getGrossMargin() {
        if (sellingPrice == null) {
            throw new RuntimeException("Prix de vente non défini");
        }
        return sellingPrice - getDishCost();
    }

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
