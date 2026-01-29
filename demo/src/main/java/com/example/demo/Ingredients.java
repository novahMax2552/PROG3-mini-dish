package com.example.demo;

import java.time.Instant;
import java.util.List;

public class Ingredients {
    private Integer id;
    private String name;
    private double price;
    private Double requiredQuantity;
    private CategoryEnum category;
    private Integer dishId;     
    private List<StockMovement> stockMovements;

    public Ingredients() {}

    public Ingredients(Integer id, String name, double price, CategoryEnum category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.requiredQuantity = null;
    }

    public Ingredients(Integer id, String name, double price, CategoryEnum category, Integer dishId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.dishId = dishId;
    }

    public Ingredients(Integer id, String name, double price, CategoryEnum category, Integer dishId, List<StockMovement> stockMovements) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.dishId = dishId;
        this.stockMovements = stockMovements;
    }


    public List<StockMovement> getStockMovements() {
        return stockMovements;
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

    public void setRequiredQuantity(Double requiredQuantity) {
        this.requiredQuantity = requiredQuantity;
    }
    
    public Double getRequiredQuantity() {
        return requiredQuantity;
    }

    public StockValue getStockValueAt(Instant instant) { 
        double total = 0.0; for (StockMovement movement : stockMovements) { 
            if (!movement.getCreationDatetime().isAfter(instant)) { 
                total += movement.getType() == MovementTypeEnum.IN ? movement.getValue().getQuantity() : -movement.getValue().getQuantity();
             } } 
             return new StockValue(total, UnitType.KG); 
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
