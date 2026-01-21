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

    public int getId() { 
        return id; 
    }
    
    public String getName() { 
        return name; 
    }
    public DishTypeEnum getDishType() { 
        return DishType; 
    }

    public List<Ingredients> getIngredients() { 
        return ingredients; 
    }
    public void setIngredients(List<Ingredients> ingredients) { 
        this.ingredients = ingredients; 
    }
    public Double getDishCost() {
        double totalCost = 0.0;

        for (Ingredients ing : ingredients) {
            if (ing.getRequiredQuantity() == null) {
                throw new RuntimeException(
                    "Quantité nécessaire inconnue pour l’ingrédient: " + ing.getName()
                );
            }
            totalCost += ing.getPrice() * ing.getRequiredQuantity();
        }

        return totalCost;
    }
}
