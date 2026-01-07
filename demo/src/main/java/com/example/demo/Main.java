package com.example.demo;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        DBConnection dbConn = new DBConnection();
        DataRetriever retriever = new DataRetriever(dbConn);
        System.out.println("=== Test findDishById ===");
        Dish dish = retriever.findDishById(1);

        if (dish != null) {
            System.out.println("Plat récupéré: " + dish.getName());

            try {
                Double cost = dish.getDishCost();
                System.out.println("Coût du plat: " + cost + " Ar");
            } catch (RuntimeException e) {
                System.out.println("Erreur lors du calcul du coût: " + e.getMessage());
            }
        } else {
            System.out.println("Aucun plat trouvé avec l'ID 1.");
        }

        System.out.println("\n=== Test saveDish ===");

        List<Ingredients> ingList = new ArrayList<>();
        ingList.add(new Ingredients(1, "Tomate", 2.0, CategoryEnum.VEGETABLE, null, 3.0));
        ingList.add(new Ingredients(2, "Fromage", 5.0, CategoryEnum.DAIRY, null, 1.0));

        Dish newDish = new Dish(0, "Pizza Margherita", DishTypeEnum.MAIN, ingList);

        Dish savedDish = retriever.saveDish(newDish);
        System.out.println("Plat sauvegardé: " + savedDish.getName() + " (ID: " + savedDish.getId() + ")");

        savedDish.setIngredients(ingList);
        Dish updatedDish = retriever.saveDish(savedDish);
        System.out.println("Plat mis à jour: " + updatedDish.getName() + " (ID: " + updatedDish.getId() + ")");
    }
}

