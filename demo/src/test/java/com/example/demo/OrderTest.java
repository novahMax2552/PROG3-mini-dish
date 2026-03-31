package com.example.demo;

import org.junit.jupiter.api.Test;

import com.example.demo.entity.Dish;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.time.Instant;
import java.util.Arrays;

public class OrderTest {

    @Test
    public void testSaveOrder_success() throws Exception {
        DataRetriever retriever = new DataRetriever();

        Dish salade = retriever.findDishById(1);
        Dish poulet = retriever.findDishById(2);

        DishOrder line1 = new DishOrder(null, salade, 2);
        DishOrder line2 = new DishOrder(null, poulet, 1);

        Order order = new Order(null, null, Instant.now(), Arrays.asList(line1, line2));

        Order savedOrder = retriever.saveOrder(order);

        assertNotNull(savedOrder.getId());
        assertTrue(savedOrder.getReference().startsWith("ORD"));
        assertEquals(3, savedOrder.getDishOrders().stream().mapToInt(DishOrder::getQuantity).sum());
        assertTrue(savedOrder.getTotalAmountWithoutVAT() > 0);
        assertTrue(savedOrder.getTotalAmountWithVAT() > savedOrder.getTotalAmountWithoutVAT());
    }

    @Test
    public void testSaveOrder_insufficientStock() {
        DataRetriever retriever = new DataRetriever();

        Dish poulet = null;
        try {
            poulet = retriever.findDishById(2);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        DishOrder line = new DishOrder(null, poulet, 9999);

        Order order = new Order(null, null, Instant.now(), Arrays.asList(line));

        Exception exception = assertThrows(RuntimeException.class, () -> retriever.saveOrder(order));
        assertTrue(exception.getMessage().contains("Stock insuffisant"));
    }

    @Test
    public void testFindOrderByReference_success() throws Exception {
        DataRetriever retriever = new DataRetriever();

        Order order = retriever.findOrderByReference("ORD00001");

        assertNotNull(order);
        assertEquals("ORD00001", order.getReference());
        assertNotNull(order.getDishOrders());
        assertTrue(order.getDishOrders().size() > 0);
    }

    @Test
    public void testFindOrderByReference_notFound() {
        DataRetriever retriever = new DataRetriever();

        Exception exception = assertThrows(RuntimeException.class, () -> retriever.findOrderByReference("ORD99999"));
        assertTrue(exception.getMessage().contains("introuvable"));
    }
}
