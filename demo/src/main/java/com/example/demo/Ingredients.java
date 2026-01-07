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

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }

    public CategoryEnum getCategory() {
        return category;
    }

    public String getDishName () {
        return dish.getName();
    }
}
