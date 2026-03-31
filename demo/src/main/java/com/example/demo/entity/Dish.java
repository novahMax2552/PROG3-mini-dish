package com.example.demo.entity;

import java.util.List;

import com.example.demo.DishType;

public class Dish {
    private Integer id;
    private String name;
    private DishTypeEnum dishType;
    private Double sellingPrice;
    private List<Ingredients> ingredients;

    public Dish(Integer id, String name, DishTypeEnum main, List<Ingredients> ingredients, Double sellingPrice) {
        this.id = id;
        this.name = name;
        this.dishType = main;
        this.ingredients = ingredients;
        this.sellingPrice = sellingPrice;
    }

    public Dish(Integer id, String name, DishTypeEnum dishType, List<Ingredients> ingredients) {
        this(id, name, dishType, ingredients, null);
    }

    public Dish(Integer id, String name, DishTypeEnum dishType, Double sellingPrice) {
        this(id, name, dishType, null, sellingPrice);
    }

    public Dish(Integer id, String name, DishTypeEnum dishType) {
        this(id, name, dishType, null, null);
    }

    public Dish() {
        //TODO Auto-generated constructor stub
    }

    public Dish(int id2, String name2, DishType start, List<Ingredients> asList, double sellingPrice2) {
        //TODO Auto-generated constructor stub
    }

    public Dish(int id2, String name2, DishTypeEnum main, List<Ingredients> asList, Object sellingPrice2) {
		//TODO Auto-generated constructor stub
	}

	public Integer getId() { return id; }
    public void setId1(long id) { this.id = (int) id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public DishTypeEnum getDishType() { return dishType; }
    public void setDishType(DishTypeEnum dishType) { this.dishType = dishType; }

    public Double getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(Double sellingPrice) { this.sellingPrice = sellingPrice; }

    public List<Ingredients> getIngredients() { return ingredients; }
    public void setIngredients(List<Ingredients> ingredients) { this.ingredients = ingredients; }

    public double getDishCost() {
        double total = 0.0;
        if (ingredients != null) {
            for (Ingredients ing : ingredients) {
                total += ing.getPrice();
            }
        }
        return total;
    }

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

    public void setId(long long1) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setId'");
    }
}
