package com.example.demo.entity;

public class Ingredients {
    private Integer id;
    private String name;
    private Double price;
    private IngredientCategoryEnum category;

    public Ingredients(Integer id, String name, Double price, IngredientCategoryEnum category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
    }

    public Ingredients(Object id2, String name2, double price2, IngredientCategoryEnum vegetable) {
        //TODO Auto-generated constructor stub
    }

    public Integer getId() {
        return id;
    }

    public void setId(long id) {
        this.id = (int) id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public IngredientCategoryEnum getCategory() {
        return category;
    }

    public void setCategory(IngredientCategoryEnum category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", category=" + category +
                '}';
    }
}

