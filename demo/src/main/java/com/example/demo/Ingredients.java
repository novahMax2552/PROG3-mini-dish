package com.example.demo;

public class Ingredients {
    private int id;
    private String name;
    private Double price;
    private CategoryEnum category;
    private Dish dish;
    private Double requiredQuantity;

    public Ingredients(int id, String name, Double price, CategoryEnum category, Dish dish, Double requiredQuantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.dish = dish;
        this.requiredQuantity = requiredQuantity;
    }

    // Getters existants
    public String getName() { return name; }
    public Double getPrice() { return price; }
    public CategoryEnum getCategory() { return category; }
    public String getDishName() { return dish != null ? dish.getName() : null; }

    // ➜ Ajouts nécessaires
    public int getId() { return id; }

    public Double getRequiredQuantity() { return requiredQuantity; }
    public void setRequiredQuantity(Double requiredQuantity) { this.requiredQuantity = requiredQuantity; }

    // (optionnel) accès au Dish si besoin
    public Dish getDish() { return dish; }
    public void setDish(Dish dish) { this.dish = dish; }
}
