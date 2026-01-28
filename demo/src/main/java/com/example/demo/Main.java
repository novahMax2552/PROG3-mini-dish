package com.example.demo;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        DBConnection dbConn = new DBConnection();
        DataRetriever retriever = new DataRetriever(dbConn);

        try {
            // a) findDishById id=1
            System.out.println("Test a) : " + retriever.findDishById(1));
            // Résultat attendu : Salade Fraîche avec Laitue et Tomate

            // b) findDishById id=999
            try {
                System.out.println("Test b) : " + retriever.findDishById(999));
            } catch (RuntimeException e) {
                System.out.println("Test b) : Exception attendue -> " + e.getMessage());
            }

            // c) findIngredients page=2 size=2
            System.out.println("Test c) : " + retriever.findIngredients(2, 2));
            // Résultat attendu : Poulet, Chocolat

            // d) findIngredients page=3 size=5
            System.out.println("Test d) : " + retriever.findIngredients(3, 5));
            // Résultat attendu : Liste vide

            // e) findDishsByIngredientName "eur"
            System.out.println("Test e) : " + retriever.findDishsByIngredientName("eur"));
            // Résultat attendu : Gâteau au chocolat

            // f) findIngredientsByCriteria category=VEGETABLE
            System.out.println("Test f) : " + retriever.findIngredientsByCriteria(null, CategoryEnum.VEGETABLE, null, 1, 10));
            // Résultat attendu : Laitue, Tomate

            // g) findIngredientsByCriteria ingredientName="cho", dishName="Sal"
            System.out.println("Test g) : " + retriever.findIngredientsByCriteria("cho", null, "Sal", 1, 10));
            // Résultat attendu : Liste vide

            // h) findIngredientsByCriteria ingredientName="cho", dishName="gâteau"
            System.out.println("Test h) : " + retriever.findIngredientsByCriteria("cho", null, "gâteau", 1, 10));
            // Résultat attendu : Chocolat

            // i) createIngredients Fromage + Oignon
            System.out.println("Test i) : " + retriever.createIngredients(Arrays.asList(
                new Ingredients(null, "Fromage", 1200.0, CategoryEnum.DAIRY),
                new Ingredients(null, "Oignon", 500.0, CategoryEnum.VEGETABLE)
            )));

            // j) createIngredients Carotte + Laitue (doublon)
            try {
                retriever.createIngredients(Arrays.asList(
                    new Ingredients(null, "Carotte", 2000.0, CategoryEnum.VEGETABLE),
                    new Ingredients(null, "Laitue", 2000.0, CategoryEnum.VEGETABLE)
                ));
            } catch (RuntimeException e) {
                System.out.println("Test j) : Exception attendue -> " + e.getMessage());
            }

            // k) saveDish Soupe de légumes
            Dish soupe = new Dish(null, "Soupe de légumes", DishType.START,
                Arrays.asList(new Ingredients(null, "Oignon", 500.0, CategoryEnum.VEGETABLE)));
            System.out.println("Test k) : " + retriever.saveDish(soupe));

            // l) saveDish mise à jour Salade fraîche
            Dish salade = new Dish(1, "Salade fraîche", DishType.START,
                Arrays.asList(
                    new Ingredients(null, "Oignon", 500.0, CategoryEnum.VEGETABLE),
                    new Ingredients(null, "Laitue", 2000.0, CategoryEnum.VEGETABLE),
                    new Ingredients(null, "Tomate", 600.0, CategoryEnum.VEGETABLE),
                    new Ingredients(null, "Fromage", 1200.0, CategoryEnum.DAIRY)
                ));
            System.out.println("Test l) : " + retriever.saveDish(salade));

            // m) saveDish mise à jour Salade de fromage
            Dish saladeFromage = new Dish(1, "Salade de fromage", DishType.START,
                Arrays.asList(new Ingredients(null, "Fromage", 1200.0, CategoryEnum.DAIRY)));
            System.out.println("Test m) : " + retriever.saveDish(saladeFromage));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
