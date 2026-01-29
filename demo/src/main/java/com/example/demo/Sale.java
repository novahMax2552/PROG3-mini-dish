package com.example.demo;

import java.time.Instant;

public class Sale {
    private Integer id;
    private Instant creationDatetime;

    public Sale(Integer id, Instant creationDatetime) {
        this.id = id;
        this.creationDatetime = creationDatetime;
    }

    public Integer getId() { return id; }
    public Instant getCreationDatetime() { return creationDatetime; }

    @Override
    public String toString() {
        return "Sale{" +
                "id=" + id +
                ", creationDatetime=" + creationDatetime +
                '}';
    }
}
