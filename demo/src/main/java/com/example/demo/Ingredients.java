package com.example.demo;

public class Ingredients {
    private Integer id;          // identifiant unique
    private String name;         // nom de l’ingrédient
    private double price;        // prix en double (NUMERIC(10,2) côté SQL)
    private CategoryEnum category; // catégorie (VEGETABLE, ANIMAL, MARINE, DAIRY, OTHER)
    private Integer dishId;      // id du plat associé (peut être null)

    // --- Constructeurs ---
    public Ingredients() {}

    public Ingredients(Integer id, String name, double price, CategoryEnum category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
    }

    public Ingredients(Integer id, String name, double price, CategoryEnum category, Integer dishId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.dishId = dishId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public CategoryEnum getCategory() {
        return category;
    }

    public void setCategory(CategoryEnum category) {
        this.category = category;
    }

    public Integer getDishId() {
        return dishId;
    }

    public void setDishId(Integer dishId) {
        this.dishId = dishId;
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", category=" + category +
                ", dishId=" + dishId +
                '}';
    }
}
