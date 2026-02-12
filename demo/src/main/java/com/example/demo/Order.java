package com.example.demo;

import java.time.Instant;
import java.util.List;

public class Order {
    private Integer id;
    private String reference;
    private Instant creationDatetime;
    private Double totalHT;
    private Double totalTTC;
    private List<DishOrder> dishOrders;
    private PaymentStatusEnum paymentStatus; 
    private Sale sale;

    public Order(Integer id, String reference, Instant creationDatetime, Double totalHT, Double totalTTC, List<DishOrder> dishOrders) {
        this.id = id;
        this.reference = reference;
        this.creationDatetime = creationDatetime;
        this.totalHT = totalHT;
        this.totalTTC = totalTTC;
        this.dishOrders = dishOrders;
    }

    // Constructeur plus simple (sans montants, calculés ensuite)
    public Order(Integer id, String reference, Instant creationDatetime, List<DishOrder> dishOrders) {
        this.id = id;
        this.reference = reference;
        this.creationDatetime = creationDatetime;
        this.dishOrders = dishOrders;
    }

public PaymentStatusEnum getPaymentStatus() { return paymentStatus; }
public void setPaymentStatus(PaymentStatusEnum paymentStatus) { this.paymentStatus = paymentStatus; }

public Sale getSale() { return sale; }
public void setSale(Sale sale) { this.sale = sale; }

public Order(Integer id, String reference, Instant creationDatetime, Double totalHT, Double totalTTC, List<DishOrder> dishOrders, PaymentStatusEnum paymentStatus, Sale sale) {
    this.id = id;
    this.reference = reference;
    this.creationDatetime = creationDatetime;
    this.totalHT = totalHT;
    this.totalTTC = totalTTC;
    this.dishOrders = dishOrders;
    this.paymentStatus = paymentStatus;
    this.sale = sale;
}


    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public Instant getCreationDatetime() { return creationDatetime; }
    public void setCreationDatetime(Instant creationDatetime) { this.creationDatetime = creationDatetime; }

    public Double getTotalHT() { return totalHT; }
    public void setTotalHT(Double totalHT) { this.totalHT = totalHT; }

    public Double getTotalTTC() { return totalTTC; }
    public void setTotalTTC(Double totalTTC) { this.totalTTC = totalTTC; }

    public List<DishOrder> getDishOrders() { return dishOrders; }
    public void setDishOrders(List<DishOrder> dishOrders) { this.dishOrders = dishOrders; }

    public Double getTotalAmountWithoutVAT() {
        double total = 0.0;
        if (dishOrders != null) {
            for (DishOrder dishOrder : dishOrders) {
                total += dishOrder.getDish().getSellingPrice() * dishOrder.getQuantity();
            }
        }
        return total;
    }

    public Double getTotalAmountWithVAT() {
        return getTotalAmountWithoutVAT() * 1.2;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", reference='" + reference + '\'' +
                ", creationDatetime=" + creationDatetime +
                ", totalHT=" + totalHT +
                ", totalTTC=" + totalTTC +
                ", dishOrders=" + dishOrders +
                '}';
    }
}
