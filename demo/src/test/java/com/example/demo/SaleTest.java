package com.example.demo;

import org.junit.jupiter.api.Test;

import com.example.demo.entity.Dish;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.time.Instant;
import java.util.Arrays;

public class SaleTest {

    @Test
    public void testCreateSaleFrom_success() throws Exception {
        DataRetriever retriever = new DataRetriever();

        Dish salade = retriever.findDishById(1);
        DishOrder line = new DishOrder(null, salade, 2);

        Order order = new Order(null, "ORD00010", Instant.now(),
                2000.0, 2400.0,
                Arrays.asList(line),
                PaymentStatusEnum.PAID,
                null);

        Sale sale = retriever.createSaleFrom(order);

        assertNotNull(sale.getId());
        assertNotNull(sale.getCreationDatetime());
    }

    @Test
    public void testCreateSaleFrom_unpaidOrder() {
        DataRetriever retriever = new DataRetriever();

        Dish salade = null;
        try {
            salade = retriever.findDishById(1);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        DishOrder line = new DishOrder(null, salade, 2);

        Order order = new Order(null, "ORD00011", Instant.now(),
                2000.0, 2400.0,
                Arrays.asList(line),
                PaymentStatusEnum.UNPAID,
                null);

        Exception exception = assertThrows(RuntimeException.class, () -> retriever.createSaleFrom(order));
        assertTrue(exception.getMessage().contains("Impossible de créer une vente"));
    }

    @Test
    public void testCreateSaleFrom_alreadyLinkedOrder() {
        DataRetriever retriever = new DataRetriever();

        Dish salade = null;
        try {
            salade = retriever.findDishById(1);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        DishOrder line = new DishOrder(null, salade, 2);

        Sale existingSale = new Sale(1, Instant.now());

        Order order = new Order(null, "ORD00012", Instant.now(),
                2000.0, 2400.0,
                Arrays.asList(line),
                PaymentStatusEnum.PAID,
                existingSale);

        Exception exception = assertThrows(RuntimeException.class, () -> retriever.createSaleFrom(order));
        assertTrue(exception.getMessage().contains("déjà associée à une vente"));
    }
}
