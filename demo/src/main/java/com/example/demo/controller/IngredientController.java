package com.example.demo.controller;

import com.example.demo.entity.Ingredients;
import com.example.demo.service.IngredientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ingredients")
public class IngredientController {
    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @GetMapping
    public List<Ingredients> getIngredients(@RequestParam int page, @RequestParam int size) {
        return ingredientService.findIngredients(page, size);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getIngredientById(@PathVariable Long id) {
        Ingredients ing = ingredientService.findById(id);
        if (ing == null) {
            return ResponseEntity.status(404).body("Ingredient.id=" + id + " is not found");
        }
        return ResponseEntity.ok(ing);
    }

    @GetMapping("/{id}/stock")
    public ResponseEntity<?> getStock(@PathVariable Long id,
                                      @RequestParam(required = false) String at,
                                      @RequestParam(required = false) String unit) {
        if (at == null || unit == null) {
            return ResponseEntity.status(400).body("Either mandatory query parameter `at` or `unit` is not provided.");
        }

        Double stockValue = ingredientService.findStockValue(id, at, unit);
        if (stockValue == null) {
            return ResponseEntity.status(404).body("Ingredient.id=" + id + " is not found");
        }

        return ResponseEntity.ok(new StockResponse(unit, stockValue));
    }

    // POST /ingredients
    @PostMapping
    public void createIngredients(@RequestBody List<Ingredients> newIngredients) {
        ingredientService.createIngredients(newIngredients);
    }

    static class StockResponse {
        public String unit;
        public Double value;

        public StockResponse(String unit, Double value) {
            this.unit = unit;
            this.value = value;
        }
    }
}
