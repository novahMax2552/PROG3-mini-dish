package com.example.demo;

import java.util.List;

public class Dish {
    private int id;
    private String name;
    private DishTypeEnum DishType;
    private List<Ingredients> ingredients;

    public Dish(int id, String name, DishTypeEnum dishType, List<Ingredients> ingredients) {
        this.id = id;
        this.name = name;
        this.DishType = dishType;
        this.ingredients = ingredients;
    }

    public String getName() {
        return name;
    }

    public DishTypeEnum getDishType() {
        return DishType;
    }

    public Double getDishCost () {
        return ingredients.stream()
                .mapToDouble(Ingredients::getPrice)
                .sum();
    }
}
