package com.example.demo.controller;

import com.example.demo.entity.Dish;
import com.example.demo.entity.Ingredients;
import com.example.demo.service.DishService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dishes")
public class DishController {
    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @GetMapping
    public List<Dish> getAllDishes() {
        return dishService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDishById(@PathVariable Long id) {
        Dish dish = dishService.findDishById(id);
        if (dish == null) {
            return ResponseEntity.status(404).body("Dish.id=" + id + " is not found");
        }
        return ResponseEntity.ok(dish);
    }

    @PutMapping("/{id}/ingredients")
    public ResponseEntity<?> updateDishIngredients(@PathVariable Long id,
                                                   @RequestBody(required = false) List<Ingredients> ingredients) {
        if (ingredients == null || ingredients.isEmpty()) {
            return ResponseEntity.status(400).body("Request body with ingredient list is mandatory.");
        }

        boolean updated = dishService.updateIngredients(id, ingredients);
        if (!updated) {
            return ResponseEntity.status(404).body("Dish.id=" + id + " is not found");
        }

        return ResponseEntity.ok("Ingredients updated for Dish.id=" + id);
    }
}
