package com.example.demo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;

public class DishTest {

    @Test
    public void testGetDishCost_SaladeFraiche() {
        Ingredients laitue = new Ingredients(null, "Laitue", 800.0, CategoryEnum.VEGETABLE);
        laitue.setRequiredQuantity(0.20); // 0.20 KG
        Ingredients tomate = new Ingredients(null, "Tomate", 600.0, CategoryEnum.VEGETABLE);
        tomate.setRequiredQuantity(0.15); // 0.15 KG

        Dish saladeFraiche = new Dish(1, "Salade fraîche", DishType.START,
                Arrays.asList(laitue, tomate), 3500.00);

        assertEquals(250.00, saladeFraiche.getDishCost(), 0.01);
        assertEquals(3250.00, saladeFraiche.getGrossMargin(), 0.01);
    }

    @Test
    public void testGetDishCost_PouletGrille() {
        Ingredients poulet = new Ingredients(null, "Poulet", 4500.0, CategoryEnum.ANIMAL);
        poulet.setRequiredQuantity(1.00); // 1 KG

        Dish pouletGrille = new Dish(2, "Poulet grillé", DishType.MAIN,
                Arrays.asList(poulet), 12000.00);

        assertEquals(4500.00, pouletGrille.getDishCost(), 0.01);
        assertEquals(7500.00, pouletGrille.getGrossMargin(), 0.01);
    }

    @Test
    public void testGetDishCost_RizAuxLegumes() {
        Dish riz = new Dish(3, "Riz aux légumes", DishType.MAIN,
            Arrays.asList(), null); // prix de vente NULL

        assertEquals(0.00, riz.getDishCost(), 0.01);
        assertThrows(RuntimeException.class, riz::getGrossMargin);
    }

    @Test
    public void testGetDishCost_GateauChocolat() {
        Ingredients chocolat = new Ingredients(null, "Chocolat", 3000.0, CategoryEnum.OTHER);
        chocolat.setRequiredQuantity(0.30); // 0.30 KG
        Ingredients beurre = new Ingredients(null, "Beurre", 2500.0, CategoryEnum.DAIRY);
        beurre.setRequiredQuantity(0.20); // 0.20 KG

        Dish gateau = new Dish(4, "Gâteau au chocolat", DishType.DESSERT,
                Arrays.asList(chocolat, beurre), 8000.00);

        assertEquals(1400.00, gateau.getDishCost(), 0.01);
        assertEquals(6600.00, gateau.getGrossMargin(), 0.01);
    }

    @Test
    public void testGetDishCost_SaladeFruits() {
        Dish saladeFruits = new Dish(5, "Salade de fruits", DishType.DESSERT,
                Arrays.asList(), null);

        assertEquals(0.00, saladeFruits.getDishCost(), 0.01);
        assertThrows(RuntimeException.class, saladeFruits::getGrossMargin);
    }
}
