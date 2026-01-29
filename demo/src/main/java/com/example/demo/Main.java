package com.example.demo;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        DBConnection dbConn = new DBConnection();
        DataRetriever retriever = new DataRetriever(dbConn);

        try {
            Dish dishA = retriever.findDishById(1);
            System.out.println("Test a) : " + dishA);
            try {
                System.out.println("Coût du plat a) : " + dishA.getDishCost());
            } catch (RuntimeException e) {
                System.out.println("Test a) : Exception attendue -> " + e.getMessage());
            }

            try {
                System.out.println("Test b) : " + retriever.findDishById(999));
            } catch (RuntimeException e) {
                System.out.println("Test b) : Exception attendue -> " + e.getMessage());
            }

            System.out.println("Test c) : " + retriever.findIngredients(2, 2));
            System.out.println("Test d) : " + retriever.findIngredients(3, 5));
            System.out.println("Test e) : " + retriever.findDishsByIngredientName("eur"));
            System.out.println("Test f) : " + retriever.findIngredientsByCriteria(null, CategoryEnum.VEGETABLE, null, 1, 10));
            System.out.println("Test g) : " + retriever.findIngredientsByCriteria("cho", null, "Sal", 1, 10));
            System.out.println("Test h) : " + retriever.findIngredientsByCriteria("cho", null, "gâteau", 1, 10));

            System.out.println("Test i) : " + retriever.createIngredients(Arrays.asList(
                new Ingredients(null, "Fromage", 1200.0, CategoryEnum.DAIRY),
                new Ingredients(null, "Oignon", 500.0, CategoryEnum.VEGETABLE)
            )));

            try {
                retriever.createIngredients(Arrays.asList(
                    new Ingredients(null, "Carotte", 2000.0, CategoryEnum.VEGETABLE),
                    new Ingredients(null, "Laitue", 2000.0, CategoryEnum.VEGETABLE)
                ));
            } catch (RuntimeException e) {
                System.out.println("Test j) : Exception attendue -> " + e.getMessage());
            }

            Dish soupe = new Dish(null, "Soupe de légumes", DishType.START,
                Arrays.asList(new Ingredients(null, "Oignon", 500.0, CategoryEnum.VEGETABLE)));
            soupe.getIngredients().get(0).setRequiredQuantity(1.0); // ⚡ quantité fixée
            soupe = retriever.saveDish(soupe);
            System.out.println("Test k) : " + soupe);
            System.out.println("Coût soupe : " + soupe.getDishCost());

            Dish salade = new Dish(1, "Salade fraîche", DishType.START,
                Arrays.asList(
                    new Ingredients(null, "Oignon", 500.0, CategoryEnum.VEGETABLE),
                    new Ingredients(null, "Laitue", 2000.0, CategoryEnum.VEGETABLE),
                    new Ingredients(null, "Tomate", 600.0, CategoryEnum.VEGETABLE),
                    new Ingredients(null, "Fromage", 1200.0, CategoryEnum.DAIRY)
                ));
            salade.getIngredients().get(0).setRequiredQuantity(1.0);
            salade.getIngredients().get(1).setRequiredQuantity(1.0);
            salade.getIngredients().get(2).setRequiredQuantity(2.0);
            salade.getIngredients().get(3).setRequiredQuantity(0.5);
            salade = retriever.saveDish(salade);
            System.out.println("Test l) : " + salade);
            System.out.println("Coût salade : " + salade.getDishCost());

            Dish saladeFromage = new Dish(1, "Salade de fromage", DishType.START,
                Arrays.asList(new Ingredients(null, "Fromage", 1200.0, CategoryEnum.DAIRY)));
            saladeFromage.getIngredients().get(0).setRequiredQuantity(1.0);
            saladeFromage = retriever.saveDish(saladeFromage);
            System.out.println("Test m) : " + saladeFromage);
            System.out.println("Coût salade fromage : " + saladeFromage.getDishCost());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
