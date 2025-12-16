package com.example.demo;

public class Ingredients {
    private int id;
    private String name;
    private Double price;
    private CategoryEnum category;
    private Dish dish;

    public Ingredients(int id, String name, Double price, CategoryEnum category, Dish dish) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.dish = dish;
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
